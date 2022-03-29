package com.nh.controller.model;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;

public class SettingSound {
    private boolean isChanged = false; // 메시지가 바꼈는지 유무 판단 Flag
    private String msg;
    private InputStream data = null;

    public SettingSound(@Nonnull String msg) {
        this.msg = msg;
        isChanged = true;
    }

    public void setMessage(@Nonnull String msg) {
        isChanged = !this.msg.equals(msg);
        this.msg = msg;
    }

    public void setChanged() {
        isChanged = true;
    }

    public String getMessage() {
        return this.msg;
    }

    public void setStream(InputStream data) throws IOException {

        synchronized (this) {
            if (this.data != null) {
                this.data.close();
                this.data = null;
            }
        }

        this.data = data;
    }

    public InputStream getStream() throws IOException {
        if (this.data == null) return null;
        this.data.reset();
        return data;
    }

    public boolean isChanged() {
        return isChanged;
    }
}
