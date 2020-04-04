package com.destro.linkcalculator.service;

import com.destro.linkcalculator.dao.StaffDao;
import com.destro.linkcalculator.dao.UpdateLogDao;
import com.destro.linkcalculator.exception.InvalidIdException;
import com.destro.linkcalculator.exception.InvalidNameUpdate;
import com.destro.linkcalculator.model.ReplacementModel;
import com.destro.linkcalculator.model.ResponseModel;
import com.destro.linkcalculator.model.UserModel;
import com.destro.linkcalculator.util.DateUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.destro.linkcalculator.util.Constants.EMPTY_FIELD;

@Service
public class StaffService {

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private UpdateLogDao updateLogDao;

    @Value("#{${edit-roles}}")
    private List<String> editRoles;

    private static final String ANONYMOUS = "anonymous";

    private Map<String, String> nameCache = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(StaffService.class);

    @PostConstruct
    public void onStartCacheNames() {
        updateNameCache();
    }

    public ResponseModel getAllStaffJsonResponse(final String batchName) {
        final List<String> list = staffDao.getALlStaff(batchName);
        if (Objects.nonNull(list)) {
            return new ResponseModel(200, new Gson().toJson(list));
        } else {
            return new ResponseModel(500, "Unable to process the request for get all staff");
        }
    }

    public ResponseModel updateNames(final ReplacementModel replacementModel) {
        try {
            if (hasEditAccessRights(replacementModel)) {

                final Map<String, String> nameMap = replacementModel.getReplaceMap();
                final Map<UserModel, UserModel> replaceMap = getModelMap(nameMap);

                final List<Map<UserModel, UserModel>> sets = new ArrayList<>();
                replaceMap.forEach((k, v) -> insertToSet(sets, k, v));

                updateNamesAsPerSets(sets, replacementModel.getReporterName());
            }
            updateNameCache();
            return new ResponseModel(200, "All names updated successfully");
        } catch (final RuntimeException e) {
            logger.error("Exception in Update name for model:{}", replacementModel, e);
            return new ResponseModel(500, e.getMessage());
        }
    }

    private boolean hasEditAccessRights(final ReplacementModel replacementModel) {
        final String id = verifyName(replacementModel.getReporterName()).getId();
        if (Objects.nonNull(id)) {
            for (final String role : editRoles) {
                if (id.contains(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateNamesAsPerSets(final List<Map<UserModel, UserModel>> sets, final String reporter) {
        sets.forEach(map -> {
            final List<UserModel> seq = new ArrayList<>();
            map.forEach((k, v) -> addToSeqForUpdate(seq, k, v));
            updateNameInReverse(seq, reporter);
        });
    }

    private void addToSeqForUpdate(final List<UserModel> seq, final UserModel k, final UserModel v) {
        if (seq.contains(k)) {
            if (seq.indexOf(k) != seq.size() - 1) {
                if (!seq.get(seq.indexOf(k) + 1).equals(v)) {
                    seq.add(seq.indexOf(k) + 1, v);
                }
            } else {
                seq.add(v);
            }
        } else if (seq.contains(v)) {
            if (seq.indexOf(v) != 0) {
                if (!seq.get(seq.indexOf(v) - 1).equals(k)) {
                    seq.add(seq.indexOf(v), k);
                }
            } else {
                seq.add(seq.indexOf(v), k);
            }
        } else {
            seq.add(k);
            seq.add(v);
        }
    }

    private void updateNameInReverse(final List<UserModel> seq, final String reporter) {
        for (int i = seq.size() - 1; i > 0; i--) {
            final UserModel x = seq.get(i - 1);
            final UserModel y = seq.get(i);
            if (staffDao.updateStaffName(x.getId(), y.getUserName())) {
                if (updateLogDao.addNewLog(reporter, x.getId(), x.getUserName(), y.getId(), y.getUserName())) {
                    //  Body no needed as nothing to do here
                } else {
                    staffDao.updateStaffName(x.getId(), x.getUserName());
                    throw new InvalidNameUpdate("Error while updating " + x.getUserName() + " with " + y
                            .getUserName());
                }
            }
        }
    }

    private void insertToSet(final List<Map<UserModel, UserModel>> sets, final UserModel k, final UserModel v) {
        boolean inserted = false;
        Map<UserModel, UserModel> map = null;

        for (int i = 0; i < sets.size(); i++) {
            final Map<UserModel, UserModel> set = sets.get(i);
            if (set.containsKey(k) || set.containsValue(v)) {
                throw new InvalidNameUpdate("Repeating Update for " + k.getUserName() + " or " + v.getUserName());
            }
            if (set.containsKey(v) || set.containsValue(k)) {
                if (inserted && sets.remove(map)) {
                    set.putAll(map);
                    continue;
                }
                set.put(k, v);
                map = set;
                inserted = true;
            }
        }

        if (!inserted) {
            final Map<UserModel, UserModel> newMap = new HashMap<>();
            newMap.put(k, v);
            sets.add(newMap);
        }
    }

    private Map<UserModel, UserModel> getModelMap(final Map<String, String> nameMap) {
        final Map<UserModel, UserModel> map = new HashMap<>();
        nameMap.forEach((k, v) -> {
            final String kId = getIdForName(k);
            String vId = getIdForName(v);

            if (kId == null || kId.isEmpty() || kId.contains(ANONYMOUS)) {
                throw new InvalidIdException(k + " does not exist in database.");
            }

            if (vId == null || vId.isEmpty()) {
                vId = getNextSubstituteId();

            } else if (vId.contains(ANONYMOUS)) {
                throw new InvalidIdException(v + " is anonymous user. Please raise issue to upgrade his account " +
                        "before updating a name with him.");
            }

            final UserModel kUser = new UserModel(kId, k, null);
            final UserModel vUser = new UserModel(vId, v, null);
            map.put(kUser, vUser);
        });
        return map;
    }

    public ResponseModel signUp(final String name) {
        final UserModel user = verifyName(name);

        if (Objects.nonNull(user.getId())) {
            final String batchName = getBatchForStaff(user.getUserName());
            user.setBatchName(batchName);
            return new ResponseModel(200, new Gson().toJson(user));
        } else {
            if (addAnonymousName(user.getUserName())) {
                final String id = getIdForName(user.getUserName());
                user.setId(id);
                user.setBatchName(ANONYMOUS);
                return new ResponseModel(200, new Gson().toJson(user));
            }
        }

        return new ResponseModel(200, "Unable to process request for sign up");
    }

    public ResponseModel register(final UserModel user) {
        final boolean isNameInserted =
                staffDao.insertName( user.getId(), user.getUserName());
        if (isNameInserted) {
            updateNameCache();
            final String id = getIdForName(user.getUserName());
            user.setId(id);
            user.setBatchName(ANONYMOUS);
            return new ResponseModel(200, new Gson().toJson(user));
        }

        return new ResponseModel(200, "Unable to process request for sign up");
    }

    public String getBatchForStaff(final String userName) {
        final String idForName = getIdForName(userName);
        if (Objects.nonNull(idForName)) {
            return idForName.split(EMPTY_FIELD)[0];
        }
        return null;
    }

    String getIdForName(final String name) {
        return staffDao.getIdForName(name);
    }

    String getNameForRelativeId(final String relativeId, final int weekNo, final int noOfMembers) {
        final String[] s = relativeId.split(EMPTY_FIELD);
        final String batchName = s[0];
        final String staffRole = s[1];
        final int staffNo = DateUtil.getOriginalWeekNo(Integer.parseInt(s[2]), weekNo, noOfMembers);

        return getNameForId(batchName + EMPTY_FIELD + staffRole + EMPTY_FIELD + staffNo);
    }

    private List<String> getAllStaff(final String batchName) {
        return staffDao.getALlStaff(batchName);
    }

    private boolean addAnonymousName(final String name) {
        final int anonymousNamesSize = getAllStaff(ANONYMOUS).size();
        final boolean isNameInserted =
                staffDao.insertName(ANONYMOUS + EMPTY_FIELD.concat(String.valueOf(anonymousNamesSize + 1)), name);
        if (isNameInserted) {
            updateNameCache();
        }
        return isNameInserted;
    }

    private String getNextSubstituteId() {
        final int substituteNamesSize = getAllStaff("substitute").size();
        return "substitute-".concat(String.valueOf(substituteNamesSize + 1));
    }

    private String getNameForId(final String id) {
        return nameCache.get(id);
    }

    private UserModel verifyName(final String name) {
        final String stripedName = name.toLowerCase()
                                       .replaceAll("\\s+", "")
                                       .replaceAll("\\p{Punct}", "");

        for (final Map.Entry<String, String> entry : nameCache.entrySet()) {
            final String stripedNames = entry.getValue().toLowerCase()
                                             .replaceAll("\\s+", "")
                                             .replaceAll("\\p{Punct}", "");
            if (stripedNames.equals(stripedName)) {
                return new UserModel(entry.getKey(), entry.getValue(), null);
            }
        }

        return new UserModel(null, name, null);
    }

    private void updateNameCache() {
        nameCache = staffDao.getAllStaffWithIds();
        logger.info("Name Cache updated");
    }
}
