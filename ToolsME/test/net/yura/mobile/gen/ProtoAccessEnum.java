package net.yura.mobile.gen;
import net.yura.tools.mobilegen.model.*;
import java.util.Hashtable;
import java.util.Vector;
import java.io.IOException;
import net.yura.mobile.io.ProtoFileUtil;
import net.yura.mobile.io.proto.CodedOutputStream;
import net.yura.mobile.io.proto.CodedInputStream;
import net.yura.mobile.io.proto.WireFormat;
/**
* THIS FILE IS GENERATED, DO NOT EDIT
*/
public class ProtoAccessEnum extends ProtoFileUtil {
    public static int getTypeEnum(String enu) {
        if ("BOBA".equals(enu)) return 1;
        if ("FRED".equals(enu)) return 2;
        if ("LALA".equals(enu)) return 3;
        return -1;
    }
    public static String getTypeString(int i) {
        switch (i) {
            case 1: return "BOBA";
            case 2: return "FRED";
            case 3: return "LALA";
            default: return unknown+i;
        }
    }
    public static int getOtherThingsTypeEnum(String enu) {
        if ("thing".equals(enu)) return 1;
        if ("stuff".equals(enu)) return 0;
        return -1;
    }
    public static String getOtherThingsTypeString(int i) {
        switch (i) {
            case 1: return "thing";
            case 0: return "stuff";
            default: return unknown+i;
        }
    }
}
