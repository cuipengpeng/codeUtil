package com.pepe.aplayer.opengl.filter;

import java.util.ArrayList;
import java.util.List;

public class CombineFilter extends AFilter {

    private List<AFilter> filters = new ArrayList<>();

    public static AFilter getCombineFilter(List<AFilter> filters) {
        return new CombineFilter(filters);
    }


    private CombineFilter(List<AFilter> filters) {
        this.filters.addAll(filters);
    }

    @Override
    public Frame draw(Frame frame) {

        for (AFilter filter : filters) {
            frame = filter.draw(frame);
        }
        return frame;
    }

    @Override
    public void destroy() {
        for (AFilter filter : filters) {
            filter.destroy();
        }
    }
}
