package com.bunny.entertainment.yousee.models.anime;

public class ServerModelOne {
    String SiteName, ServerName;

    public ServerModelOne(String siteName, String serverName) {
        SiteName = siteName;
        ServerName = serverName;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }
}
