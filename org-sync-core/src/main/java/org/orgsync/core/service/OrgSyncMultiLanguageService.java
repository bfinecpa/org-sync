package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.type.TargetDomain;

public interface OrgSyncMultiLanguageService {

    default  void create(List<MultiLanguageDto> multiLanguageDtos) {}

    // 존재하지 않는 다국어를 삭제 요청하는 경우가 존재합니다.
    default void delete(Long id, TargetDomain targetDomain) {}

    default void delete(List<MultiLanguageDto> multiLanguageDtos) {}

    default void update(List<MultiLanguageDto> multiLanguageDto) {}

}
