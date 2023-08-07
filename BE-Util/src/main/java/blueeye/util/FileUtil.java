package blueeye.util;

import blueeye.config.AlertConfig;
import blueeye.config.BlueEyeConfig;
import blueeye.pojo.user.User;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author SDJin
 * @CreationDate 2023/8/7 20:11
 * @Description ：
 */
public class FileUtil {

    /**
     * 读取BlueEye配置文件
     *
     * @param sr
     * @param name
     * @return
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static BlueEyeConfig readBlueEyeConfig(SAXReader sr, String name) throws FileNotFoundException, DocumentException {
        InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(name);
        Document doc = sr.read(inputStream);
        Element rootElement = doc.getRootElement();

        BlueEyeConfig blueEyeConfig = new BlueEyeConfig();
        //解析用户信息

        blueEyeConfig.setUsers((List<User>) rootElement.element("users").elements().stream().map(object -> {
            Element element = (Element) object;
            User user = new User();
            user.setName(element.elementText("name"));
            user.setPassword(element.elementText("password"));
            user.setRole(Integer.parseInt(element.elementText("role")));
            return user;
        }).collect(Collectors.toList()));
        //解析requestEye扫描路径
        blueEyeConfig.setScanPackageName(rootElement.elementText("scanPackageName"));
        //解析rdb配置
        blueEyeConfig.setRdbPath(rootElement.element("rdb").elementText("path"));
        blueEyeConfig.setRdbFileNum(Integer.parseInt(rootElement.element("rdb").elementText("fileNum")));
        //解析持久化配置
        Element persistence = rootElement.element("persistence");
        blueEyeConfig.setProperties(persistence.elementText("properties"));
        if ("default".equals(persistence.elementText("mode"))) {
            blueEyeConfig.setManager("blueeye.persistent.defaultImpl.JdbcMapperManager");
            blueEyeConfig.setAlertMapper("blueeye.persistent.defaultImpl.JdbcAlertRecordMapper");
            blueEyeConfig.setInstanceMapper("blueeye.persistent.defaultImpl.JdbcInstanceMapper");
            blueEyeConfig.setMetricMapper("blueeye.persistent.defaultImpl.JdbcMetricDataMapper");
            blueEyeConfig.setTaskMapper("blueeye.persistent.defaultImpl.JdbcTaskMapper");
        } else {
            blueEyeConfig.setManager(persistence.elementText("manager"));
            blueEyeConfig.setAlertMapper(persistence.elementText("alertMapper"));
            blueEyeConfig.setInstanceMapper(persistence.elementText("instanceMapper"));
            blueEyeConfig.setMetricMapper(persistence.elementText("metricMapper"));
            blueEyeConfig.setTaskMapper(persistence.elementText("taskMapper"));
        }
        //解析时间轮配置
        blueEyeConfig.setTimeSpan(Long.parseLong(rootElement.element("timeWheel").elementText("timeSpan")));
        blueEyeConfig.setWheelSize(Integer.parseInt(rootElement.element("timeWheel").elementText("size")));
        //解析任务执行器配置
        blueEyeConfig.setCoreThread(Integer.parseInt(rootElement.elementText("coreThread")));
        //解析实例重试次数
        blueEyeConfig.setRetryNum(Integer.parseInt(rootElement.elementText("retryNum")));
        System.out.println(blueEyeConfig);
        return blueEyeConfig;
    }

    /**
     * 读取报警配置文件
     *
     * @param sr
     * @param name
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public static AlertConfig readAlertConfig(SAXReader sr, String name) throws DocumentException, FileNotFoundException {
        InputStream alertFile = FileUtil.class.getClassLoader().getResourceAsStream(name);
        Document alert = sr.read(alertFile);
        AlertConfig alertConfig = new AlertConfig();
        Element root = alert.getRootElement();
        Element wx = root.element("wx");
        alertConfig.setWxToken(wx.attributeValue("wxToken"));
        alertConfig.setWxAppid(wx.attributeValue("wxAppid"));
        alertConfig.setWxSecret(wx.attributeValue("wxSecret"));
        alertConfig.setWxTemplateId(wx.attributeValue("wxTemplateId"));
        Element note = root.element("note");
        alertConfig.setAccessKey(note.attributeValue("accessKey"));
        alertConfig.setAccessKeySecret(note.attributeValue("accessKeySecret"));
        alertConfig.setSignName(note.attributeValue("signName"));
        alertConfig.setTemplateCode(note.attributeValue("templateCode"));
        Element mail = root.element("mail");
        alertConfig.setMailUser(mail.attributeValue("mailUser"));
        alertConfig.setMailPassword(mail.attributeValue("mailPassword"));
        alertConfig.setQueueCapacity(Integer.parseInt(root.elementText("queueCapacity")));
        return alertConfig;
    }

}
