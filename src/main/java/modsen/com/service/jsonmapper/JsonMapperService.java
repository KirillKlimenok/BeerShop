package modsen.com.service.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JsonMapperService {
    <T> T getObj(String json, Class<T> tClass) throws JsonProcessingException;
}
