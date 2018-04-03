package com.jf.jlfund.inter;

/**
 * 为了能在onSuccess()够拦截到带有头布局的列表 Bean中嵌套的list为空，需要实现该接口，并在isEmpty中返回 list.isNestListEmpty();
 * <p>
 * 该接口是为了适配在某些页面中，接口将该页面的头数据【例如佣金总和】与列表数据【佣金列表】一起返回、从而无法拦截到列表是否为空的情况。
 * 为了解决该问题、内部嵌套List的 DTO 应该实现该接口并重写该方法： return nestList.isEmpty();
 * </p>
 * Created by 55 on 2017/11/6.
 */

public interface NestList {
    boolean isNestListEmpty();
}
