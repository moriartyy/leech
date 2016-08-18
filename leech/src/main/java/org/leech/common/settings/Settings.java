package org.leech.common.settings;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.net.InetSocketAddress;
import java.util.*;

public class Settings {
    
    public static Settings Empty = new Settings();
    
    private Map<String, String> map;
    
    private Settings() {
        this(Maps.<String, String>newHashMap());
    }
    
    public Settings(Properties source) {
    	this(Maps.fromProperties(source));
    }
    
    public Settings(Map<String, String> source) {
        this.map = new HashMap<>(Preconditions.checkNotNull(source));
    }
    
    public String get(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        return map.get(key);
    }
    
    public void merge(Settings target) {
        this.map.putAll(target.map);
    }
    
    public Map<String, String> asMap() {
        return ImmutableMap.copyOf(map);
    }
    
    public Settings getSettingsByPrefix(String prefix) {
    	Map<String, String> subSettings = new HashMap<>();
        for (Map.Entry<String, String> entry : subSettings.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                subSettings.put(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
        return new Settings(subSettings);
    }
    
    public Map<String, String> getAsMap(String name, Map<String, String> defVal) {
        String paramVal = get(name);
        if (paramVal == null) {
            return defVal;
        } else {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            
            if (paramVal == "")
                return builder.build();
            
            String[] kvpairs = paramVal.split(",");
            for (String kvpair : kvpairs) {
                String[] parts = kvpair.split(":");
                if (parts.length == 1) {
                    builder.put(parts[0].trim(), "");
                } else {
                    builder.put(parts[0].trim(), parts[1].trim());
                }
            }
            return builder.build();
        }
    }
    
    public InetSocketAddress[] getServerNodes(String key) {
        return getServerNodes(key, null);
    }

    public InetSocketAddress[] getServerNodes(String key, InetSocketAddress[] defaultVal) {
        String v = get(key);
        if (v == null) {
            return defaultVal;
        }

        List<String> hostAddressList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(v);
        InetSocketAddress[] nodes = new InetSocketAddress[hostAddressList.size()];
        int index = 0;
        for(String hostAddress : hostAddressList) {
            String[] hostAddressComponents = hostAddress.split(":");
            String host = hostAddressComponents[0].trim();
            int port = Integer.valueOf(hostAddressComponents[1].trim());
            nodes[index++] = new InetSocketAddress(host, port);
        }
        return nodes;
    }
    
    public Iterable<String> getAsIterable(String key) {
        return getAsIterable(key, Sets.<String>newHashSet());
    }
    
    public Iterable<String> getAsIterable(String key, Iterable<String> defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Splitter.on(',').trimResults().omitEmptyStrings().split(v);
    }

    public String get(String key, String def) {
        String v = get(key);
        return v == null ? def : v;
    }
    
    public int getAsInt(String key, int defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Integer.parseInt(v);
    }

    public long getAsLong(String key, long defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Long.parseLong(v);
    }
    
    public double getAsDouble(String key, double defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Double.parseDouble(v);
    }
    
    public float getAsFloat(String key, float defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Float.parseFloat(v);
    }
   
    public boolean getAsBoolean(String key, boolean defaultVal) {
        String v = get(key);
        return v == null ? defaultVal : Boolean.parseBoolean(v);
    }
    
    public static Builder builder() {
    	return new Builder();
    }
    
    static class Builder {
    	
    	private Map<String, String> map = new LinkedHashMap<>();
    	
    	public Builder() {
    	}
    	
    	public Builder put(Settings settings) {
    		map.putAll(settings.map);
    		return this;
    	}
    	
    	public Builder put(Properties settings) {
            for (String name : settings.stringPropertyNames()) {
                map.put(name, settings.getProperty(name));
            }
    		return this;
    	}
    	
    	public Builder put(Map<String, String> settings) {
    		map.putAll(settings);
    		return this;
    	}
    	
    	public Builder put(String key, String value) {
    		map.put(key, value);
    		return this;
        }
        
    	public Settings build() {
    		return new Settings(map);
    	}
    }

}
