package com.hems.project.ADMIN_SERVICE.service;

import org.springframework.stereotype.Component;

@Component
public class AutoCaseConsumerService {

    //take from redis if any site or vpp heartbeat is missing then
    //we fetch that siteId or vppId and automatically create ticket

    //and if same site thi continue message ave che so we not create new ticket we
    //only update  new event on that ticket ..
    

    //

}
