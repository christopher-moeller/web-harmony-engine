package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.model.ApplicationStartupLogDto;
import com.webharmony.core.data.jpa.model.AppApplicationStartupLog;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStartupLogService extends AbstractEntityCrudService<ApplicationStartupLogDto, AppApplicationStartupLog> {


}
