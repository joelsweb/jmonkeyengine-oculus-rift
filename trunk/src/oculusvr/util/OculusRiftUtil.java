/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oculusvr.util;

import com.jme3.math.Matrix4f;
import com.oculusvr.capi.EyeRenderDesc;
import com.oculusvr.capi.FovPort;
import com.oculusvr.capi.Hmd;
import com.oculusvr.capi.OvrLibrary;
import com.oculusvr.capi.OvrMatrix4f;
import com.oculusvr.capi.OvrSizei;
import com.oculusvr.capi.RenderAPIConfig;

/**
 *
 * @author reden
 */
public class OculusRiftUtil {

    private static boolean maxFOV = false, dis_vig = false;
    private static boolean customFOV = false;
    private static FovPort[] customFovPorts;
    
    public static void useMaxEyeFov(boolean enable) {
        maxFOV = enable;
    }
    
    public static void disableVignette(boolean disable) {
        dis_vig = disable;
    }
    
    public static EyeRenderDesc[] configureRendering(Hmd hmd, int width, int height, int samples) {
        EyeRenderDesc[] configureResult;
        
        FovPort fovPorts[] = (FovPort[]) new FovPort().toArray(2);
        
        if( maxFOV ) {
            fovPorts[0] = hmd.MaxEyeFov[0];
            fovPorts[1] = hmd.MaxEyeFov[1];
        } else if (customFOV) {
            fovPorts[0] = hmd.DefaultEyeFov[0];
            fovPorts[1] = hmd.DefaultEyeFov[1];
            applyFovPort(customFovPorts[0], fovPorts[0]);
            applyFovPort(customFovPorts[1], fovPorts[1]);
        } else {
            fovPorts[0] = hmd.DefaultEyeFov[0];
            fovPorts[1] = hmd.DefaultEyeFov[1];
        }
        
        RenderAPIConfig rc = new RenderAPIConfig();
        rc.Header.API = OvrLibrary.ovrRenderAPIType.ovrRenderAPI_OpenGL;
        rc.Header.RTSize = new OvrSizei(width, height);
        rc.Header.Multisample = samples;

        int distortionCaps;
        
        if( dis_vig ) {
            distortionCaps =   OvrLibrary.ovrDistortionCaps.ovrDistortionCap_Chromatic
                             | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_TimeWarp
                             | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_Overdrive;                    
        } else {
            distortionCaps =   OvrLibrary.ovrDistortionCaps.ovrDistortionCap_Chromatic
                             | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_TimeWarp
                             | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_Vignette
                             | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_Overdrive;            
        }
        
        configureResult = hmd.configureRendering(rc, distortionCaps, fovPorts);

        if (null == configureResult) {
            throw new IllegalStateException("Unable to configure rendering");
        }
        return configureResult;
    }
    
    public static Matrix4f toMatrix4f(OvrMatrix4f m) {
        return new Matrix4f(m.M).transpose();
    }
    
    public static void setCustomFovPorts(FovPort[] fovPorts){
        customFOV = true;
        customFovPorts = fovPorts;
    }
    
    private static void applyFovPort(FovPort source, FovPort target){
        target.LeftTan = source.LeftTan;
        target.RightTan = source.RightTan;
        target.UpTan = source.UpTan;
        target.DownTan = source.DownTan;
        
    }
}
