package com.webharmony.core.service.data;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.webcontent.WebContentAreaDto;
import com.webharmony.core.data.jpa.model.webcontent.AppWebContentArea;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContentArea;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebContentAreaService extends AbstractEntityCrudService<WebContentAreaDto, AppWebContentArea> {

    private static final QAppWebContentArea qAppWebContentArea = QAppWebContentArea.appWebContentArea;

    @PersistenceContext
    private EntityManager em;

    public AppWebContentArea findAreaByUniqueNameOrThrow(String uniqueName) {
        final AppWebContentArea entity = new JPAQuery<>(em)
                .select(qAppWebContentArea)
                .from(qAppWebContentArea)
                .where(qAppWebContentArea.uniqueName.eq(uniqueName))
                .fetchOne();

        return Optional.ofNullable(entity)
                .orElseThrow(() -> new NotFoundException(String.format("Area with unique name %s not found", uniqueName)));
    }
}
