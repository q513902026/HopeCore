package me.hope;


import me.hope.core.inject.annotation.Inject;

public class VersionCheck {
    @Inject
    private static HopeCore hopeCore;

    public static boolean checkCoreVersion(int afterVersion0,int afterVersion1,int afterVersion2){
        String verStr = hopeCore.getDescription().getVersion();
        String[] versionsStr = verStr.split("\\.");
        int[] versions = new int[3];
        for (int index = 0; index < versionsStr.length; index++) {
            versions[index] = Integer.parseInt(versionsStr[index]);
        }
        if (versions[0] < afterVersion0) {
            return false;
        }else if (versions[1] <afterVersion1 ){
            return false;
        }else if (versions[2]<afterVersion2){
            return false;
        }
        return true;
    }
}
