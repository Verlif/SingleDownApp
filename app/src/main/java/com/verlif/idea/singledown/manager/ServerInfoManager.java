package com.verlif.idea.singledown.manager;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.verlif.idea.singledown.model.ServerInfo;
import com.verlif.idea.singledown.tools.SPTool;

import java.util.ArrayList;
import java.util.List;

public class ServerInfoManager {

    private static ServerInfoManager instance;

    private static final String SP_NAME = "server";
    private static final String VALUE_SERVER_IP = "ip";
    private static final String VALUE_SERVER_LIST = "serverList";

    private SPTool spTool;

    private ServerInfoManager(Context context) {
        spTool = new SPTool(context, SP_NAME);
    }

    public static ServerInfoManager newInstance(Context context) {
        if (instance == null) {
            synchronized (ServerInfoManager.class) {
                if (instance == null) {
                    instance = new ServerInfoManager(context);
                }
            }
        }
        return instance;
    }

    public List<ServerInfo> getServerInfoList() {
        String s = spTool.loadString(VALUE_SERVER_LIST, "");
        if (!s.equals("")) {
            return JSON.parseArray(s, ServerInfo.class);
        } else return new ArrayList<>();
    }

    public void saveServerList(List<ServerInfo> serverInfoList) {
        JSONArray json = new JSONArray();
        json.addAll(serverInfoList);
        spTool.saveValue(VALUE_SERVER_LIST, json.toString());
    }

    public String getRootUrl() {
        return spTool.loadString(VALUE_SERVER_IP, "http://");
    }

    public void saveRootUrl(String rootUrl) {
        spTool.saveValue(VALUE_SERVER_IP, rootUrl);
    }
}
