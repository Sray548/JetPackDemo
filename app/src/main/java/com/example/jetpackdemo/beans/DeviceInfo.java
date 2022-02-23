package com.example.jetpackdemo.beans;

public class DeviceInfo {
    private Media media;
    private Carinfo carinfo;
    private Wifi wifi;
    private Devinfo devinfo;
    private long sdfree;
    private long sdtotal;
    private String sdfsformat;
    private int module;
    private String language;
    private int aicalibrated;
    private double height;
    private double car_width;
    private int cam_to_front;
    private int fcw_level;
    private double distance;
    private int cam_disconn;
    private boolean ota_success;

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Carinfo getCarinfo() {
        return carinfo;
    }

    public void setCarinfo(Carinfo carinfo) {
        this.carinfo = carinfo;
    }

    public Wifi getWifi() {
        return wifi;
    }

    public void setWifi(Wifi wifi) {
        this.wifi = wifi;
    }

    public Devinfo getDevinfo() {
        return devinfo;
    }

    public void setDevinfo(Devinfo devinfo) {
        this.devinfo = devinfo;
    }

    public long getSdfree() {
        return sdfree;
    }

    public void setSdfree(long sdfree) {
        this.sdfree = sdfree;
    }

    public long getSdtotal() {
        return sdtotal;
    }

    public void setSdtotal(long sdtotal) {
        this.sdtotal = sdtotal;
    }

    public String getSdfsformat() {
        return sdfsformat;
    }

    public void setSdfsformat(String sdfsformat) {
        this.sdfsformat = sdfsformat;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getAicalibrated() {
        return aicalibrated;
    }

    public void setAicalibrated(int aicalibrated) {
        this.aicalibrated = aicalibrated;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCar_width() {
        return car_width;
    }

    public void setCar_width(double car_width) {
        this.car_width = car_width;
    }

    public int getCam_to_front() {
        return cam_to_front;
    }

    public void setCam_to_front(int cam_to_front) {
        this.cam_to_front = cam_to_front;
    }

    public int getFcw_level() {
        return fcw_level;
    }

    public void setFcw_level(int fcw_level) {
        this.fcw_level = fcw_level;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCam_disconn() {
        return cam_disconn;
    }

    public void setCam_disconn(int cam_disconn) {
        this.cam_disconn = cam_disconn;
    }

    public boolean isOta_success() {
        return ota_success;
    }

    public void setOta_success(boolean ota_success) {
        this.ota_success = ota_success;
    }

    public static class Media {
        private int lockprev;
        private int lockpost;
        private Front Front;
        private Front Rear;
        private int gsensorsensitivity;
        private int audioMute;
        private int alertVolume;
        private int callVolume;

        public int getLockprev() {
            return lockprev;
        }

        public void setLockprev(int lockprev) {
            this.lockprev = lockprev;
        }

        public int getLockpost() {
            return lockpost;
        }

        public void setLockpost(int lockpost) {
            this.lockpost = lockpost;
        }

        public Front getFront() {
            return Front;
        }

        public void setFront(Front Front) {
            this.Front = Front;
        }

        public Front getRear() {
            return Rear;
        }

        public void setRear(Front Rear) {
            this.Rear = Rear;
        }

        public int getGsensorsensitivity() {
            return gsensorsensitivity;
        }

        public void setGsensorsensitivity(int gsensorsensitivity) {
            this.gsensorsensitivity = gsensorsensitivity;
        }

        public int getAudioMute() {
            return audioMute;
        }

        public void setAudioMute(int audioMute) {
            this.audioMute = audioMute;
        }

        public int getAlertVolume() {
            return alertVolume;
        }

        public void setAlertVolume(int alertVolume) {
            this.alertVolume = alertVolume;
        }

        public int getCallVolume() {
            return callVolume;
        }

        public void setCallVolume(int callVolume) {
            this.callVolume = callVolume;
        }

        public static class Front {
            private int ch;
            private int w;
            private int h;
            private int fps;
            private int bitrate;
            private int prev;
            private int post;
            private int encoder;

            public int getCh() {
                return ch;
            }

            public void setCh(int ch) {
                this.ch = ch;
            }

            public int getW() {
                return w;
            }

            public void setW(int w) {
                this.w = w;
            }

            public int getH() {
                return h;
            }

            public void setH(int h) {
                this.h = h;
            }

            public int getFps() {
                return fps;
            }

            public void setFps(int fps) {
                this.fps = fps;
            }

            public int getBitrate() {
                return bitrate;
            }

            public void setBitrate(int bitrate) {
                this.bitrate = bitrate;
            }

            public int getPrev() {
                return prev;
            }

            public void setPrev(int prev) {
                this.prev = prev;
            }

            public int getPost() {
                return post;
            }

            public void setPost(int post) {
                this.post = post;
            }

            public int getEncoder() {
                return encoder;
            }

            public void setEncoder(int encoder) {
                this.encoder = encoder;
            }
        }
    }

    public static class Carinfo {
        private int fueltanksize;
        private double enginedisplacement;
        private int cartype;
        private int harshBreakingThreshold;
        private int harshCorneringThreshold;

        public int getFueltanksize() {
            return fueltanksize;
        }

        public void setFueltanksize(int fueltanksize) {
            this.fueltanksize = fueltanksize;
        }

        public double getEnginedisplacement() {
            return enginedisplacement;
        }

        public void setEnginedisplacement(double enginedisplacement) {
            this.enginedisplacement = enginedisplacement;
        }

        public int getCartype() {
            return cartype;
        }

        public void setCartype(int cartype) {
            this.cartype = cartype;
        }

        public int getHarshBreakingThreshold() {
            return harshBreakingThreshold;
        }

        public void setHarshBreakingThreshold(int harshBreakingThreshold) {
            this.harshBreakingThreshold = harshBreakingThreshold;
        }

        public int getHarshCorneringThreshold() {
            return harshCorneringThreshold;
        }

        public void setHarshCorneringThreshold(int harshCorneringThreshold) {
            this.harshCorneringThreshold = harshCorneringThreshold;
        }
    }

    public static class Wifi {
        private String ssid;
        private String ip;
        private String mac;
        private boolean _$5GMode;

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public boolean is_$5GMode() {
            return _$5GMode;
        }

        public void set_$5GMode(boolean _$5GMode) {
            this._$5GMode = _$5GMode;
        }
    }

    public static class Devinfo {
        private String version = "1.2.3";
        private String model;
        private String ltemodule;
        private String product;
        private String sn;
        private String uboot;
        private int feature;
        private String flashsize;
        private String mcu;
        private String manu;
        private boolean waytronic2;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getLtemodule() {
            return ltemodule;
        }

        public void setLtemodule(String ltemodule) {
            this.ltemodule = ltemodule;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getUboot() {
            return uboot;
        }

        public void setUboot(String uboot) {
            this.uboot = uboot;
        }

        public int getFeature() {
            return feature;
        }

        public void setFeature(int feature) {
            this.feature = feature;
        }

        public String getFlashsize() {
            return flashsize;
        }

        public void setFlashsize(String flashsize) {
            this.flashsize = flashsize;
        }

        public String getMcu() {
            return mcu;
        }

        public void setMcu(String mcu) {
            this.mcu = mcu;
        }

        public String getManu() {
            return manu;
        }

        public void setManu(String manu) {
            this.manu = manu;
        }

        public boolean isWaytronic2() {
            return waytronic2;
        }

        public void setWaytronic2(boolean waytronic2) {
            this.waytronic2 = waytronic2;
        }
    }
}
