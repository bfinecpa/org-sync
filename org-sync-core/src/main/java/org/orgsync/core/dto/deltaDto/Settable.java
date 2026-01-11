package org.orgsync.core.dto.deltaDto;

import org.orgsync.core.dto.LogInfoDto;

public interface Settable{

    default void set(LogInfoDto logInfoDto) {}


}
