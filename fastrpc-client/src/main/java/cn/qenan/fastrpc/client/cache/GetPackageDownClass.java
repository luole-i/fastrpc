package cn.qenan.fastrpc.client.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据包名获取包名下的类
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/21
 */
@Component
public class GetPackageDownClass implements ResourceLoaderAware {
    private static ResourceLoader resourceLoader;

    private List<String> list = new ArrayList<String>();
    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> get(String path) throws IOException {
        ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        Resource[] resources = resolver.getResources(path);
        for(Resource r : resources){
            MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
            list.add(reader.getClassMetadata().getClassName());
        }
        return list;
    }
}
