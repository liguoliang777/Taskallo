package com.jzt.hol.android.jkda.sdk.bean.main;

import java.util.List;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class DiscoverListBean {
    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private DailyNewGamesListBean dailyNewGamesList;
        private WeeklyNewGamesListBean weeklyNewGamesList;
        private AgentGamePicListBean agentGamePicList;
        private List<GameCategroyListBean> gameCategroyList;
        private List<ResultListBean> resultList;

        public DailyNewGamesListBean getDailyNewGamesList() {
            return dailyNewGamesList;
        }

        public void setDailyNewGamesList(DailyNewGamesListBean dailyNewGamesList) {
            this.dailyNewGamesList = dailyNewGamesList;
        }

        public WeeklyNewGamesListBean getWeeklyNewGamesList() {
            return weeklyNewGamesList;
        }

        public void setWeeklyNewGamesList(WeeklyNewGamesListBean weeklyNewGamesList) {
            this.weeklyNewGamesList = weeklyNewGamesList;
        }

        public AgentGamePicListBean getAgentGamePicList() {
            return agentGamePicList;
        }

        public void setAgentGamePicList(AgentGamePicListBean agentGamePicList) {
            this.agentGamePicList = agentGamePicList;
        }

        public List<GameCategroyListBean> getGameCategroyList() {
            return gameCategroyList;
        }

        public void setGameCategroyList(List<GameCategroyListBean> gameCategroyList) {
            this.gameCategroyList = gameCategroyList;
        }

        public List<ResultListBean> getResultList() {
            return resultList;
        }

        public void setResultList(List<ResultListBean> resultList) {
            this.resultList = resultList;
        }

        public static class DailyNewGamesListBean {
            /**
             * list : []
             * categoryName : 每日新发现
             * categoryId : -1
             */

            private String categoryName;
            private int categoryId;
            private List<DiscoverTopBean> list;

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public List<DiscoverTopBean> getList() {
                return list;
            }

            public void setList(List<DiscoverTopBean> list) {
                this.list = list;
            }
        }

        public static class WeeklyNewGamesListBean {
            /**
             * list : []
             * categoryName : 近期最热
             * categoryId : -2
             */

            private String categoryName;
            private int categoryId;
            private List<DiscoverTopBean> list;

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public List<DiscoverTopBean> getList() {
                return list;
            }

            public void setList(List<DiscoverTopBean> list) {
                this.list = list;
            }
        }

        public static class AgentGamePicListBean {
            /**
             * list : []
             * categoryName : 大厂
             * categoryId : -3
             */

            private String categoryName;
            private int categoryId;
            private List<DiscoverTopBean> list;

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public List<DiscoverTopBean> getList() {
                return list;
            }

            public void setList(List<DiscoverTopBean> list) {
                this.list = list;
            }
        }

        public static class GameCategroyListBean {
            /**
             * id : 7
             * cName : 角色
             * cParentId : 2
             */

            private int id;
            private String cName;
            private String cParentId;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCName() {
                return cName;
            }

            public void setCName(String cName) {
                this.cName = cName;
            }

            public String getCParentId() {
                return cParentId;
            }

            public void setCParentId(String cParentId) {
                this.cParentId = cParentId;
            }
        }

        public static class ResultListBean {
            /**
             * list : [{"id":591,"cName":null,"gameName":"影之刃2","gameLogo":"http://oss.ngame.cn/upload/1483083079423.png",
             * "scoreLevel":null,"gameLink":null,"gameSize":null,"versionCode":null,"packages":null,"filename":null,"md5":null,
             * "versionName":null,"updateTime":null},{"id":672,"cName":null,"gameName":"龙之谷","gameLogo":"","scoreLevel":null,
             * "gameLink":null,"gameSize":null,"versionCode":null,"packages":null,"filename":null,"md5":null,
             * "versionName":null,"updateTime":null}]
             * categoryName : 角色
             * categoryId : 7
             */

            private String categoryName;
            private int categoryId;
            private List<ListBean> list;

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                /**
                 * id : 591
                 * cName : null
                 * gameName : 影之刃2
                 * gameLogo : http://oss.ngame.cn/upload/1483083079423.png
                 * scoreLevel : null
                 * gameLink : null
                 * gameSize : null
                 * versionCode : null
                 * packages : null
                 * filename : null
                 * md5 : null
                 * versionName : null
                 * updateTime : null
                 */

                private int id;
                private Object cName;
                private String gameName;
                private String gameLogo;
                private Object scoreLevel;
                private Object gameLink;
                private Object gameSize;
                private Object versionCode;
                private Object packages;
                private Object filename;
                private Object md5;
                private Object versionName;
                private Object updateTime;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public Object getCName() {
                    return cName;
                }

                public void setCName(Object cName) {
                    this.cName = cName;
                }

                public String getGameName() {
                    return gameName;
                }

                public void setGameName(String gameName) {
                    this.gameName = gameName;
                }

                public String getGameLogo() {
                    return gameLogo;
                }

                public void setGameLogo(String gameLogo) {
                    this.gameLogo = gameLogo;
                }

                public Object getScoreLevel() {
                    return scoreLevel;
                }

                public void setScoreLevel(Object scoreLevel) {
                    this.scoreLevel = scoreLevel;
                }

                public Object getGameLink() {
                    return gameLink;
                }

                public void setGameLink(Object gameLink) {
                    this.gameLink = gameLink;
                }

                public Object getGameSize() {
                    return gameSize;
                }

                public void setGameSize(Object gameSize) {
                    this.gameSize = gameSize;
                }

                public Object getVersionCode() {
                    return versionCode;
                }

                public void setVersionCode(Object versionCode) {
                    this.versionCode = versionCode;
                }

                public Object getPackages() {
                    return packages;
                }

                public void setPackages(Object packages) {
                    this.packages = packages;
                }

                public Object getFilename() {
                    return filename;
                }

                public void setFilename(Object filename) {
                    this.filename = filename;
                }

                public Object getMd5() {
                    return md5;
                }

                public void setMd5(Object md5) {
                    this.md5 = md5;
                }

                public Object getVersionName() {
                    return versionName;
                }

                public void setVersionName(Object versionName) {
                    this.versionName = versionName;
                }

                public Object getUpdateTime() {
                    return updateTime;
                }

                public void setUpdateTime(Object updateTime) {
                    this.updateTime = updateTime;
                }
            }
        }
    }
}
