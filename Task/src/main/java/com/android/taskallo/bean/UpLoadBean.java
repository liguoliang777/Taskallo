package com.android.taskallo.bean;

/**
 * Created by Administrator on 2016/12/31 0031.
 */

public class UpLoadBean {

    /**
     * code : 0
     * msg : success!
     * data : {"fileName":"28","fileSize":217,"nickName":"11",
     * "boardId":"bo130ce7b5b08d4ed29a512b5681cb01e6","fileUrl":"",
     * "id":"lg9a2c3c9c66f44e879907adbea2a46b5d","operation":"添加了附件","content":"28",
     * "fileId":"fiad2c784cb6cf42debd88ea9f8dc3119c"}
     */

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
        /**
         * fileName : 28
         * fileSize : 217
         * nickName : 11
         * boardId : bo130ce7b5b08d4ed29a512b5681cb01e6
         * fileUrl :
         * id : lg9a2c3c9c66f44e879907adbea2a46b5d
         * operation : 添加了附件
         * content : 28
         * fileId : fiad2c784cb6cf42debd88ea9f8dc3119c
         */

        private String fileName;
        private int fileSize;
        private String nickName;
        private String boardId;
        private String fileUrl;
        private String id;
        private String operation;
        private String content;
        private String fileId;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getFileSize() {
            return fileSize;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getBoardId() {
            return boardId;
        }

        public void setBoardId(String boardId) {
            this.boardId = boardId;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }
}
