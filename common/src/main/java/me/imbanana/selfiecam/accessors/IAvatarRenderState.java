package me.imbanana.selfiecam.accessors;

import org.apache.commons.lang3.NotImplementedException;

public interface IAvatarRenderState {
    default boolean selfieCam$isCameraEntity() {
        throw new NotImplementedException();
    }

    default void selfieCam$setCameraEntity(boolean value) {
        throw new NotImplementedException();
    }
}
