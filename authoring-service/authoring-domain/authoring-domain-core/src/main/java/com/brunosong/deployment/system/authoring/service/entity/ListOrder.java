package com.brunosong.deployment.system.authoring.service.entity;

import java.util.Collections;
import java.util.List;

/**
 * 목록 안의 자리를 순서로 쓰는 단계(학년, 학기, 단원, 차시)를 한 칸 옮긴다.
 * 순서 칸을 따로 두지 않는다. 칸을 두면 목록의 자리와 칸 값이 어긋날 수 있어서, 순서는 한 곳(자리)에만 둔다
 */
final class ListOrder {

    private ListOrder() {
    }

    /** 한 칸 옮긴다. 이미 맨 위(또는 맨 아래)라 옮길 곳이 없으면 아무것도 하지 않고 false */
    static <T> boolean move(List<T> list, T item, MoveDirection direction) {
        int from = list.indexOf(item);
        int to = direction == MoveDirection.UP ? from - 1 : from + 1;
        if (from < 0 || to < 0 || to >= list.size()) {
            return false;
        }
        Collections.swap(list, from, to);
        return true;
    }
}
