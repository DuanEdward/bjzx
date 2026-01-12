package com.hebei.cs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hebei.cs.entity.Contact;
import com.hebei.cs.mapper.ContactMapper;
import com.hebei.cs.service.ContactService;
import org.springframework.stereotype.Service;

/**
 * 联系我们服务实现类
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact> implements ContactService {
}

