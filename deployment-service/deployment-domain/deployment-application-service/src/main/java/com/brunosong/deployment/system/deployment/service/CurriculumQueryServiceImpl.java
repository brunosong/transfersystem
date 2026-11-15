package com.brunosong.deployment.system.deployment.service;

import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumSummaryView;
import com.brunosong.deployment.system.deployment.service.dto.query.CurriculumTreeView;
import com.brunosong.deployment.system.deployment.service.dto.query.PageView;
import com.brunosong.deployment.system.deployment.service.entity.SourceConfig;
import com.brunosong.deployment.system.deployment.service.handler.SourceFindDataHandler;
import com.brunosong.deployment.system.deployment.service.helper.deployment.DeploymentDataConvertHelper;
import com.brunosong.deployment.system.deployment.service.helper.source.SourceConfigHelper;
import com.brunosong.deployment.system.deployment.service.ports.input.service.CurriculumQueryService;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceDocumentPage;
import com.brunosong.deployment.system.deployment.service.ports.output.repository.SourceRepository;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceConfigId;
import com.brunosong.deployment.system.deployment.service.valueobject.SourceId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculumQueryServiceImpl implements CurriculumQueryService {

    private final SourceConfigHelper sourceConfigHelper;
    private final SourceRepository sourceRepository;
    private final SourceFindDataHandler sourceFindDataHandler;
    private final DeploymentDataConvertHelper deploymentDataConvertHelper;

    @Override
    public PageView<CurriculumSummaryView> findCurriculums(Long sourceConfigId, String keyword, int page, int size) {
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(new SourceConfigId(sourceConfigId));
        SourceDocumentPage found = sourceRepository.findPage(sourceConfig, keyword, page, size);

        // 접수 때와 같은 변환기를 거쳐야 목록의 과목 수와 실제로 나뉘는 청크가 맞는다
        List<CurriculumSummaryView> items = found.documents().stream()
                .map(deploymentDataConvertHelper::toCurriculum)
                .map(CurriculumSummaryView::from)
                .toList();
        int totalPages = (int) ((found.totalItems() + size - 1) / size);
        return new PageView<>(items, page, size, found.totalItems(), totalPages);
    }

    @Override
    public CurriculumTreeView findCurriculum(Long sourceConfigId, String curriculumId) {
        SourceConfig sourceConfig = sourceConfigHelper.findSourceConfigInfo(new SourceConfigId(sourceConfigId));
        return CurriculumTreeView.from(sourceFindDataHandler.findCurriculum(new SourceId(curriculumId), sourceConfig));
    }
}
