package org.elsa.fileservice.api.response.adapter;

import lombok.Data;

import java.util.Map;

/**
 * @author valor
 * @date 2018/11/21 16:58
 */
@Data
public class FileSavedResult {

    private Map<String, String> saved;

    private Map<String, String> failed;
}
