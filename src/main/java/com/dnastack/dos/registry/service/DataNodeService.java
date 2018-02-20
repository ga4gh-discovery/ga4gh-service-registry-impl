package com.dnastack.dos.registry.service;

import com.dnastack.dos.registry.controller.Ga4ghDataNodeDto;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Demo implementation ONLY
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Service
public class DataNodeService {

    public List<Ga4ghDataNodeDto> getNodes(String authorization, String name, String alias, String description, String providerInformation, String pageToken, Integer pageSize) {

        Ga4ghDataNodeDto node1 = new Ga4ghDataNodeDto(){{
            setId("1");
            setName("SickKids");
            setCreated(DateTime.now());
            setHttpAddress("http://sickkids.com/public/data");
            setProviderInformation("Data provided by SickKids");
        }};

        Ga4ghDataNodeDto node2 = new Ga4ghDataNodeDto(){{
            setId("2");
            setName("MountSinai");
            setCreated(DateTime.now());
            setHttpAddress("http://mountsinai.com/public/data");
            setProviderInformation("Data provided by Mount Sinai Hospital");
        }};

        return Arrays.asList(node1, node2);
    }
}
