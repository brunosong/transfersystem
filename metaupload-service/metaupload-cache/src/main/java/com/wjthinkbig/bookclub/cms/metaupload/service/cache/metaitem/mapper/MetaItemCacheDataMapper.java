package com.wjthinkbig.bookclub.cms.metaupload.service.cache.metaitem.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjthinkbig.bookclub.cms.metaupload.service.cache.metaitem.exception.MetaItemCacheException;
import com.wjthinkbig.bookclub.cms.metaupload.service.entity.MetaItem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MetaItemCacheDataMapper {

    private final ObjectMapper objectMapper;

    public MetaItemCacheDataMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String metaItemListToJsonString(List<MetaItem> metaItemList) {
        String json = "";
        try {
            json = objectMapper.writeValueAsString(metaItemList);
        } catch (JsonProcessingException e) {
            log.error("MetaItemList to Json ObjectMapper Error : {}", metaItemList);
            throw new MetaItemCacheException("MetaItemList to Json ObjectMapper Error");
        }
        return json;
    }

    public List<MetaItem> jsonToMetaItemList(String json) {
        List<MetaItem> metaItems;
        try {
            metaItems = objectMapper.readValue(json, new TypeReference<List<MetaItem>>() {});
        } catch (JsonProcessingException e) {
            log.error("Json To MetaItemList ObjectMapper Error : {}", json);
            throw new MetaItemCacheException("Json To MetaItemList ObjectMapper Error", e);
        }
        return metaItems;
    }
}
