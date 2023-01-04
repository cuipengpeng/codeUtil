package com.pepe.aplayer.opengl.filter;

import static android.opengl.GLES20.glBindTexture;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.opengl.GLES11Ext;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.TextureUtil;
import com.pepe.aplayer.view.fragment.Camera2Fragment;


public class CamerPreviewFilter extends AFilter {

    private int oesTextureId=-1;
    public CamerPreviewFilter(Context context) {
        super(context, R.raw.single_input_v, R.raw.camera_prev_f);
//        coord=Transform.TEXTURE_FLIP_HORIZONTAL_AND_NO_ROTATION;
        updateOesTextureCoordinate();
    }

    public void updateOesTextureCoordinate(){
        hasInit=false;
        if (Camera2Fragment.mCurrentCameraId == CameraCharacteristics.LENS_FACING_BACK) {
            coord=Transform.TEXTURE_FLIP_HORIZONTAL_AND_ROTATED_90;
        } else {
            coord=Transform.TEXTURE_ROTATED_90;
        }
    }

    public int getOesTextureId(){
        if(oesTextureId==-1){
            oesTextureId = TextureUtil.createOesTextureId();
        }
        return oesTextureId;
    }

    @Override
    protected void bindTexture(int textureId) {
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
    }
}
