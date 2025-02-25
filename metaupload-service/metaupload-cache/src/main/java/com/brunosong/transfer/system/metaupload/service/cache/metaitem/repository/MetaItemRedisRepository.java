package com.brunosong.transfer.system.metaupload.service.cache.metaitem.repository;

import com.brunosong.transfer.system.metaupload.service.cache.metaitem.exception.MetaItemCacheNotFoundException;
import com.brunosong.transfer.system.metaupload.service.cache.metaitem.mapper.MetaItemCacheDataMapper;
import com.brunosong.transfer.system.metaupload.service.entity.MetaItem;
import com.wjthinkbig.bookclub.cms.valueobject.MetaUploadRedisConstKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class MetaItemRedisRepository {

    private final RedisTemplate<String,Object> redisTemplate;
    private final MetaItemCacheDataMapper metaItemCacheDataMapper;

    public MetaItemRedisRepository(RedisTemplate<String, Object> redisTemplate,
                                   MetaItemCacheDataMapper metaItemCacheDataMapper) {
        this.redisTemplate = redisTemplate;
        this.metaItemCacheDataMapper = metaItemCacheDataMapper;
    }

    public String saveMetaItemList(List<MetaItem> metaItemList) {
        String key = MetaUploadRedisConstKeys.META_UPLOAD_KEY + UUID.randomUUID();
        redisTemplate.opsForHash().put(key,
                                       MetaUploadRedisConstKeys.META_UPLOAD_ORIGINAL_FIELD_KEY,
                                       metaItemCacheDataMapper.metaItemListToJsonString(metaItemList)
        );
        return key;
    }

    public List<MetaItem> getOriginalMetaList(String key){
        Object object = redisTemplate.opsForHash().get(key, MetaUploadRedisConstKeys.META_UPLOAD_ORIGINAL_FIELD_KEY);
        if (object != null) {
            List<MetaItem> metaItems = metaItemCacheDataMapper.jsonToMetaItemList((String) object);
            return metaItems;
        } else {
            log.error("This key cannot be found in Redis! : {}", key);
            throw new MetaItemCacheNotFoundException("This key cannot be found in Redis! : " + key);
        }
    }

}
