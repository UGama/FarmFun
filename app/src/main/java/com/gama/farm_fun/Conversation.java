package com.gama.farm_fun;

import com.facebook.drawee.view.SimpleDraweeView;

public class Conversation {
    public String friendId;
    public SimpleDraweeView headPic;
    public String headPicUrl;
    public String friendName;

    public Conversation(String friendId, String friendName, String headPicUrl) {
        this.friendName = friendName;
        this.friendId = friendId;
        this.headPicUrl = headPicUrl;
    }
}
