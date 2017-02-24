package com.github.charleslzq.loghub.converter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by Charles on 2017/2/24.
 */
@Data
@AllArgsConstructor
public class LogData {
    private Map<String, String> contents;
}
