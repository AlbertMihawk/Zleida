package com.zleidadr.sugarDb;

import com.zleidadr.common.Constant;
import com.zleidadr.entity.Address;
import com.zleidadr.entity.Appoint;
import com.zleidadr.entity.AppointCount;
import com.zleidadr.entity.AppointDetail;
import com.zleidadr.entity.Dict;
import com.zleidadr.entity.Receivable;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.entity.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoxuli on 16/1/29.
 */
public class DbManager {

//    private static final int COUNT_PRE_PAGE = 2;


    public static void saveProjectAppointCountDb(List<AppointCount> appointCountList) {
        for (AppointCount count : appointCountList) {
            count.save();
        }
    }

    public static List<AppointCount> getProjectAppointCountDb() {
        return AppointCount.listAll(AppointCount.class);
    }

    public static void saveProjectAppointListDb(List<Appoint> appointList) {
        for (Appoint appoint : appointList) {
            appoint.save();
        }
    }

    public static List<Appoint> getProjectAppointListDb() {
        return Appoint.listAll(Appoint.class);
    }

    public static void saveProjectAppointDetailDb(AppointDetail appointDetail) {
        appointDetail.save();
    }

    public static AppointDetail getProjectAppointDetailDb(String projectId) {
        if (projectId != null) {
            List<AppointDetail> list = Appoint.find(AppointDetail.class, "project_id = ?", projectId);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

    public static void saveProjectAddressListDb(List<Address> addressList) {
        for (Address address : addressList) {
            address.save();
        }
    }

    public static List<Address> getProjectAddressListDb() {
        return Address.listAll(Address.class);
    }

    public static void saveDictMapDb(Map<String, List<Dict>> map) {
        Iterator<Map.Entry<String, List<Dict>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            List<Dict> list = iterator.next().getValue();
            for (Dict dict : list) {
                dict.save();
            }
        }
    }

    public static Map<String, List<Dict>> getDictMapDb() {
        HashMap<String, List<Dict>> map = new HashMap<>();
        String[] consArray = new String[]{Constant.DICTNAME_ACCESS_OBJECT,
                Constant.DICTNAME_ADDRESS_VALIDITY,
                Constant.DICTNAME_ACCESS_RESULT_SELF,
                Constant.DICTNAME_ACCESS_RESULT_OTHER};
        for (String key : consArray) {
            List<Dict> tempList = Dict.find(Dict.class, "dict_name = ?", key);
            if (tempList != null) {
                map.put(key, tempList);
            }
        }
        return map;
    }

    public static void saveReceivableListDb(List<Receivable> list) {
        for (Receivable receivable : list) {
            receivable.save();
        }
    }

    public static List<Receivable> getReceivableListDb() {
        return Receivable.listAll(Receivable.class);
    }

    public static void saveReceivableReqDb(ReceivableReq receivableReq, int state) {
        receivableReq.setState(state);
        receivableReq.save();
        List<Resource> list = receivableReq.getResourcesList();
        if (list != null) {
            for (Resource resource : list) {
                resource.save();
            }
        }
    }

    public static List<ReceivableReq> getReceivableReqListDb(int state) {

        List<ReceivableReq> list = ReceivableReq.find(ReceivableReq.class, "state = ?", new String[]{state + ""}, null, " id desc", null);//"by id
        // desc"
//        for (ReceivableReq receivableReq : list) {
//            receivableReq.setResourcesList(Resource.find(Resource.class, "receivable_req = ?", receivableReq.getLocalId() + ""));
//        }
        return list;
    }

    public static void removeResources(ReceivableReq receivableReq, List<File> files, Integer[] nums) {
        ArrayList<Resource> list = (ArrayList<Resource>) receivableReq.getResourcesList();
        Iterator<Resource> iter = list.iterator();
        while (iter.hasNext()) {
            Resource res = iter.next();
            for (Integer num : nums) {
                String fileName = files.get(num).getName();
                if (fileName.equals(res.getResourceOriginal())) {
                    iter.remove();
                    Resource.delete(res);
                    break;
                }
            }
        }
    }

    public static void removeResources(ReceivableReq receivableReq, String fileName) {
        ArrayList<Resource> list = (ArrayList<Resource>) receivableReq.getResourcesList();
        Iterator<Resource> iter = list.iterator();
        while (iter.hasNext()) {
            Resource res = iter.next();
            if (fileName.equals(res.getResourceOriginal())) {
                iter.remove();
                Resource.delete(res);
                break;
            }
        }
    }

    public static void clearDbData() {
        Address.deleteAll(Address.class);
        Appoint.deleteAll(Appoint.class);
        AppointCount.deleteAll(AppointCount.class);
        AppointDetail.deleteAll(AppointDetail.class);
        Dict.deleteAll(Dict.class);
        Receivable.deleteAll(Receivable.class);
        ReceivableReq.deleteAll(ReceivableReq.class);
        Resource.deleteAll(Resource.class);
    }
}
