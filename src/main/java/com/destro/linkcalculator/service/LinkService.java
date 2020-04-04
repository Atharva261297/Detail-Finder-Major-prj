package com.destro.linkcalculator.service;

import com.destro.linkcalculator.builder.VisualModelBuilder;
import com.destro.linkcalculator.dao.LinkDao;
import com.destro.linkcalculator.exception.LinkCalculationException;
import com.destro.linkcalculator.model.InternalUserModel;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.model.TrainLinkPair;
import com.destro.linkcalculator.model.UserRole;
import com.destro.linkcalculator.model.VisualModel;
import com.destro.linkcalculator.util.DateUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;
import static com.destro.linkcalculator.util.Constants.REST_LINK;
import static com.destro.linkcalculator.util.Constants.UNKNOWN_GET_LINK_ERROR_RESPONSE;

@Service
public class LinkService {

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private BatchesService batchesService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private VisualModelBuilder visualModelBuilder;

    private final Logger logger = LoggerFactory.getLogger(LinkService.class);

    public ResponseModel getLinkForStaffByName(final String userName, final LocalDate date) {
        try {
            final InternalUserModel tempModel = createTempModelForExecution( userName );

            final String startDate = batchesService.getStartForBatch( tempModel.getBatchName() );
            final Integer noOfMembers = batchesService.getNoOfMembers( tempModel.getBatchName() );

            if ( Objects.nonNull( startDate ) && Objects.nonNull( noOfMembers ) ) {

                final int weekNo = DateUtil.getWeekNo( startDate, date, noOfMembers );
                final String day = DateUtil.getWeekDay( java.sql.Date.valueOf( date ) );
                final int relativeWeekNo = DateUtil.getRelativeWeekNo( tempModel.getStaffNo(), weekNo, noOfMembers );

                final TrainLinkPair staffLink = getTrainLinkById( createId( tempModel, relativeWeekNo ), day);

                if ( Objects.nonNull( staffLink ) ) {
                    String coStaffName = "-";

                    if (!REST_LINK.equals(staffLink.getTrain())) {
                        coStaffName = getCoStaffName( staffLink, weekNo, day, noOfMembers,
                                tempModel.getBatchName(), tempModel.getRole() );
                    }

                    final VisualModel visualModel = visualModelBuilder.buildSingleLinkForStaff( tempModel, staffLink,
                                                                                            date, coStaffName);
                    return new ResponseModel(200, new Gson().toJson( visualModel ) );
                } else {
                    throw new LinkCalculationException("StaffLink cannot be null {user-" + tempModel + "; date-" + date + "}");
                }
            }
        } catch (final RuntimeException e) {
            logger.error("Exception while calculating link for staff: {} on date: {}", userName, date, e);
            return new ResponseModel(500, UNKNOWN_GET_LINK_ERROR_RESPONSE);
        }
        return new ResponseModel(500, UNKNOWN_GET_LINK_ERROR_RESPONSE);
    }

    public ResponseModel getLinkForBatch(final String batchName, final LocalDate date) {
        try {
            final List<VisualModel> sheet = new ArrayList<>();
            final String startDate = batchesService.getStartForBatch( batchName );
            final Integer noOfMembers = batchesService.getNoOfMembers( batchName );

            if ( Objects.nonNull( startDate ) && Objects.nonNull( noOfMembers ) ) {

                final int weekNo = DateUtil.getWeekNo( startDate, date, noOfMembers );
                final String day = DateUtil.getWeekDay( java.sql.Date.valueOf( date ) );
                final String nextDay = DateUtil.getWeekDay( java.sql.Date.valueOf( date.plusDays(1) ) );

                for (int i = 1; i <= 16; i++) {
                    final String lpmId = batchName + EMPTY_FIELD + UserRole.LPM.name() + EMPTY_FIELD + i;
                    final String colpmId = batchName + EMPTY_FIELD + UserRole.COLPM.name() + EMPTY_FIELD + i;

                    visualModelBuilder.insertLpmToVisual(sheet, lpmId,
                            staffService.getNameForRelativeId(lpmId, weekNo, noOfMembers), day, nextDay);

                    visualModelBuilder.insertColpmToVisual(sheet, colpmId,
                            staffService.getNameForRelativeId(colpmId, weekNo, noOfMembers), day, nextDay);
                }
            }

            if (sheet.isEmpty()) {
                return new ResponseModel(500, UNKNOWN_GET_LINK_ERROR_RESPONSE);
            } else {
                return new ResponseModel(200, new Gson().toJson(sheet));
            }
        } catch (final RuntimeException e) {
            logger.error("Exception while calculating link for batch: {} on date: {}", batchName, date, e);
            return new ResponseModel(500, UNKNOWN_GET_LINK_ERROR_RESPONSE);
        }
    }

    private String getCoStaffName(final TrainLinkPair staffLink, final int weekNo, final String day,
                                  final int noOfMembers, final String batchName, final UserRole role) {
        final String secondStaffRelativeId;
        if (UserRole.LPM.equals(role)) {
            secondStaffRelativeId = linkDao.getStaffByTrainLink(day, staffLink,
                    batchName + EMPTY_FIELD + UserRole.COLPM.name());
        } else if (UserRole.COLPM.equals(role)) {
            secondStaffRelativeId = linkDao.getStaffByTrainLink(day, staffLink,
                    batchName + EMPTY_FIELD + UserRole.LPM.name());
        } else {
            throw new LinkCalculationException("Invalid role in getCoStaffName; role-" + role);
        }


        return staffService.getNameForRelativeId(secondStaffRelativeId, weekNo, noOfMembers);
    }

    private TrainLinkPair getTrainLinkById(final String id, final String day) {
        return linkDao.getTrainAndLinkById(day, id);
    }

    private InternalUserModel createTempModelForExecution(final String userName) {
        final String staffId = staffService.getIdForName( userName );
        final String[] s = staffId.split( EMPTY_FIELD );
        return new InternalUserModel( Integer.parseInt( s[2] ), s[0], UserRole.valueOf( s[1] ), staffId, userName );
    }

    private String createId(final InternalUserModel tempModel, final int relativeWeekNo) {
        return tempModel.getBatchName() + EMPTY_FIELD + tempModel.getRole() + EMPTY_FIELD + relativeWeekNo;
    }
}
