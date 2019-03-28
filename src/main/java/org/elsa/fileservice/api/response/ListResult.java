package org.elsa.fileservice.api.response;

import java.util.List;

/**
 * @author valor
 * @date 2018/9/20 17:20
 */
public class ListResult<T> extends BaseResult {

    private Integer total;
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public ListResult<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public int getTotal() {
        if (total == null) {
            if (null != list) {
                return list.size();
            }
            return 0;
        }
        return total;
    }

    public ListResult<T> setTotal(int total) {
        this.total = total;
        return this;
    }
}
