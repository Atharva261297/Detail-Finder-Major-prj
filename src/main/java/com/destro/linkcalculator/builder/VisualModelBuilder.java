package com.destro.linkcalculator.builder;

import com.destro.linkcalculator.dao.LinkDao;
import com.destro.linkcalculator.dao.TrainTimeDao;
import com.destro.linkcalculator.exception.LinkCalculationException;
import com.destro.linkcalculator.model.InternalUserModel;
import com.destro.linkcalculator.model.TrainLinkPair;
import com.destro.linkcalculator.model.UserRole;
import com.destro.linkcalculator.model.VisualModel;
import com.destro.linkcalculator.model.Weekdays;
import com.destro.linkcalculator.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;
import static com.destro.linkcalculator.util.Constants.MULTIPLE_TRAIN_NO_PATTERN;
import static com.destro.linkcalculator.util.Constants.REST_LINK;
import static com.destro.linkcalculator.util.Constants.RETURN_TO_NGP;
import static com.destro.linkcalculator.util.Constants.SEPARATOR;

@Component
public class VisualModelBuilder {

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private TrainTimeDao trainTimeDao;

    public VisualModel buildSingleLinkForStaff(final InternalUserModel tempModel, final TrainLinkPair staffLink,
                                               final LocalDate date, final String coStaffName) throws LinkCalculationException {

        if ( Objects.nonNull( staffLink ) ) {

            if (REST_LINK.equals( staffLink.getTrain() ) ) {
                return buildRestModel( tempModel.getUserName(), tempModel.getRole() );

            } else {
                final String nextDay = DateUtil.getWeekDay( java.sql.Date.valueOf( date.plusDays(1) ) );

                if ( UserRole.LPM.equals( tempModel.getRole() ) ) {
                    return build( staffLink, tempModel.getUserName(), coStaffName, tempModel.getId(), nextDay );

                } else if (UserRole.COLPM.equals( tempModel.getRole() ) ) {
                    return build( staffLink, coStaffName, tempModel.getUserName(), tempModel.getId(), nextDay );
                } else {
                    throw new LinkCalculationException("Invalid role-" + tempModel.getRole() + " for user-" + tempModel.getUserName());
                }
            }
        } else {
            throw new LinkCalculationException("StaffLink cannot be null {user-" + tempModel + "; date-" + date + "}");
        }
    }

    public void insertLpmToVisual(final List<VisualModel> sheet, final String lpmId, final String lpmName,
                                  final String day, final String nextDay) throws LinkCalculationException {

        final TrainLinkPair trainLinkPair = linkDao.getTrainAndLinkById(day, lpmId);

        if ( REST_LINK.equals( trainLinkPair.getTrain() ) ) {
            sheet.add( buildRestModel( lpmName, UserRole.LPM ) );

        } else {
            for ( final VisualModel vm : sheet ) {

                if ( vm.getTrainNo().equals( trainLinkPair.getTrain() ) &&
                        vm.getLink().equals( trainLinkPair.getLink() ) &&
                        vm.getLpmName().equals( EMPTY_FIELD ) ) {

                    vm.setLpmName( lpmName );
                    return;
                }
            }
            sheet.add( build( trainLinkPair, lpmName, EMPTY_FIELD, lpmId, nextDay) );
        }
    }

    public void insertColpmToVisual(final List<VisualModel> sheet, final String colpmId, final String colpmName,
                                    final String day, final String nextDay) throws LinkCalculationException {

        final TrainLinkPair trainLinkPair = linkDao.getTrainAndLinkById(day, colpmId);

        if ( REST_LINK.equals( trainLinkPair.getTrain() ) ) {
            sheet.add( buildRestModel( colpmName, UserRole.COLPM ) );

        } else {
            for ( final VisualModel vm : sheet ) {
                if ( vm.getTrainNo().equals( trainLinkPair.getTrain() ) &&
                        vm.getLink().equals( trainLinkPair.getLink() ) &&
                        vm.getColpmName().equals( EMPTY_FIELD ) ) {

                    vm.setColpmName( colpmName );
                    return;
                }
            }

            sheet.add( build( trainLinkPair, EMPTY_FIELD, colpmName, colpmId, nextDay ) );
        }
    }

    private VisualModel build(final TrainLinkPair trainLinkPair, final String lpmName,
                              final String colpmName, final String id, final String day) {

        final String time = trainTimeDao.getTimeForTrainLink(trainLinkPair);

        final String returnTrain = getReturnTrain( trainLinkPair , day, getNextDayId(id, day) );

        return new VisualModel( trainLinkPair.getTrain(), time, trainLinkPair.getLink(),
                lpmName, colpmName, returnTrain );

    }

    private VisualModel buildRestModel(final String userName, final UserRole role) throws LinkCalculationException {

        if ( Objects.isNull( userName ) || StringUtils.isBlank( userName ) ) {
            throw new LinkCalculationException("UserName cannot be null");

        } else {

            if ( UserRole.LPM.equals( role ) ) {
                return new VisualModel( REST_LINK, EMPTY_FIELD, EMPTY_FIELD, userName, EMPTY_FIELD, EMPTY_FIELD );

            } else if ( UserRole.COLPM.equals( role ) ) {
                return new VisualModel( REST_LINK, EMPTY_FIELD, EMPTY_FIELD, EMPTY_FIELD, userName, EMPTY_FIELD );

            } else {
                throw new LinkCalculationException("Invalid role-" + role + " for user-" + userName);
            }
        }
    }

    private String getReturnTrain(final TrainLinkPair trainLink, final String day, final String nextDayId) {

        if (trainLink.getTrain().matches(MULTIPLE_TRAIN_NO_PATTERN)) {
            return trainLink.getTrain().split(SEPARATOR)[2];

        } else if (!trainLink.getLink().endsWith(RETURN_TO_NGP)) {
            return linkDao.getTrainAndLinkById(day, nextDayId).getTrain();
        }
        return EMPTY_FIELD;
    }

    private String getNextDayId(String id, final String day) {

        if ( Weekdays.monday.name().equals( day ) ) {

            final String[] split = id.split( EMPTY_FIELD );
            int nextId = Integer.parseInt( split[2] ) + 1;
            if ( nextId == 17 ) {
                nextId = 1;
            }
            id = split[0] + "-" + split[1] + "-" + nextId;
        }
        return id;
    }
}
