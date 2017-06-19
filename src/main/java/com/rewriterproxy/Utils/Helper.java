package com.rewriterproxy.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

public class Helper {


    /**
     * Return HashMap from Vector vectors table data
     *
     * @param dataVector
     * @return
     */
    public static Map<String, String> getMapFromTableVector(Vector dataVector) {
        Map<String, String> result = new HashMap<>();
        dataVector.stream()
                .forEachOrdered(new Consumer<Vector>() {
                    @Override
                    public void accept(Vector v) {
                        if (v.get(0) != null) {
                            if (v.get(1) == null) {
                                result.put(v.get(0).toString(), "");
                            } else {
                                result.put(v.get(0).toString(), v.get(1).toString());
                            }
                        }
                    }
                });

        return result;
    }


}
