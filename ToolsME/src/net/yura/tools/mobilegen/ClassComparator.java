package net.yura.tools.mobilegen;

import java.util.Comparator;
import net.yura.tools.mobilegen.ProtoLoader.MessageDefinition;

/**
 * @author Yura Mamyrin
 */
public class ClassComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Class class1 = null;
        Class class2 = null;;
        if (o1 instanceof MessageDefinition) {
            class1 = ((MessageDefinition)o1).getImplementation();
        }
        else if (o1 instanceof Class) {
            class1 = (Class)o1;
        }
        if (o2 instanceof MessageDefinition) {
            class2 = ((MessageDefinition)o2).getImplementation();
        }
        else if (o2 instanceof Class) {
            class2 = (Class)o2;
        }

        if (class1 == class2) {
            return 0;
        }
        if (class1 == null) {
            return -1;
        }
        if (class2 == null) {
            return 1;
        }

        Class s1 = class1;
        while (s1!=null) {
            s1 = s1.getSuperclass();
            if (s1 == class2) {
                return -1;
            }
        }
        Class s2 = class2;
        while (s2!=null) {
            s2 = s2.getSuperclass();
            if (s2 == class1) {
                return 1;
            }
        }

        return class1.getName().compareTo(class2.getName());
    }

}
