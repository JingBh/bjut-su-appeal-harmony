package tech.bjut.appeal.data.model;

import tech.bjut.appeal.ResourceTable;

public enum CampusEnum {
    MAIN,
    TONGZHOU,
    ZHONGLAN;

    public static int toString(CampusEnum campus) {
        switch (campus) {
            case MAIN:
                return ResourceTable.String_campus_main;
            case TONGZHOU:
                return ResourceTable.String_campus_tongzhou;
            case ZHONGLAN:
                return ResourceTable.String_campus_zhonglan;
            default:
                return -1;
        }
    }
}
