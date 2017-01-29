package org.petermfc.service;

import com.github.wtekiela.opensub4j.api.OpenSubtitles;
import com.github.wtekiela.opensub4j.impl.OpenSubtitlesImpl;
import com.github.wtekiela.opensub4j.response.SubtitleInfo;
import org.apache.xmlrpc.XmlRpcException;
import org.petermfc.exception.CredentialsException;
import org.petermfc.exception.SubtitleException;
import org.petermfc.model.ProviderConfig;
import org.petermfc.model.Subtitle;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.petermfc.Globals.Setting.*;

public class OpenSubtitlesProvider implements SubtitlesProvider {
    public static final String USERAGENT = "OSTestUserAgentTemp";
    private static final String SERVICE_URL = "http://api.opensubtitles.org:80/xml-rpc";
    private Function<Class<?>, ProviderConfig> configFun;
    private OpenSubtitles openSubtitles;

    public OpenSubtitlesProvider() {
        try {
            openSubtitles = new OpenSubtitlesImpl(new URL(SERVICE_URL));
            openSubtitles.login("eng", USERAGENT);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setConfigurationCallback(Function<Class<?>, ProviderConfig> fun) {
        this.configFun = fun;
    }

    @Override
    public Collection<Subtitle> findSubtitles(String query, Collection<String> languages) throws SubtitleException, CredentialsException {
        ProviderConfig config = configFun.apply(this.getClass());
        if(config != null) {
            if(Boolean.valueOf(config.getSetting(USE_CUSTOM_LOGIN_DATA))) {
                String user = config.getSetting(USER_NAME);
                char[] p = config.getPassword();
                if(p == null) {
                    throw new CredentialsException("You have entered incurrect user password!");
                }
                try {
                    openSubtitles.logout();
                    openSubtitles.login(user, Arrays.toString(p), "eng", USERAGENT);
                } catch (XmlRpcException e) {
                    throw new SubtitleException(e);
                }
            } else if(Boolean.valueOf(config.getSetting(USE_CUSTOM_AGENT))) {
                String userAgent = config.getSetting(USER_AGENT);
                try {
                    openSubtitles.logout();
                    openSubtitles.login(userAgent, "eng");
                } catch (XmlRpcException e) {
                    throw new SubtitleException(e);
                }
            }
        }
        Collection<Subtitle> ret = new ArrayList<>();
        try {
            for(String lang : languages) {
                List<SubtitleInfo> infos = openSubtitles.searchSubtitles(lang, query, "", "");
                for (SubtitleInfo info : infos) {
                    ret.add(new Subtitle(info));
                }
            }
        } catch (XmlRpcException e) {
            throw new SubtitleException(e);
        }
        return ret;
    }
}
