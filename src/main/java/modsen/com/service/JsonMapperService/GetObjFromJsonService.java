package modsen.com.service.JsonMapperService;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface GetObjFromJsonService {
    <T> T getObj(String json, Class<T> tClass) throws JsonProcessingException;
}
