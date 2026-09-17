package com.overdrive.app.camera.dilink5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.overdrive.app.camera.CameraProfile;
import com.overdrive.app.camera.CameraProfiles;
import com.overdrive.app.camera.CameraRole;

import org.junit.Before;
import org.junit.Test;

public class DiLink5PlatformHelperTest {

    @Before
    public void reset() {
        DiLink5PlatformHelper.resetForTests();
    }

    @Test
    public void sharkSelectedModelIsShark() {
        assertTrue(DiLink5PlatformHelper.isSharkProfile("shark6"));
    }

    @Test
    public void sealionSelectedModelIsNotShark() {
        assertFalse(DiLink5PlatformHelper.isSharkProfile("sealion7"));
    }

    @Test
    public void sealionProfileWithoutExplicitModelDoesNotBlockDxf() {
        // Persisted dilink5_sealion7 from old auto-detect must not win when the
        // unit is DXF Shark and the user did not pick Sealion. We cannot set
        // SystemProperties here; just ensure explicit sealion model still wins.
        assertFalse(DiLink5PlatformHelper.isSharkProfile("BYD Sealion 7"));
        DiLink5PlatformHelper.clearCachedProfile();
        assertTrue(DiLink5PlatformHelper.isSharkProfile("shark6"));
    }

    @Test
    public void aisByteForViewModeMatchesDoc() {
        assertEquals(4, DiLink5PlatformHelper.aisByteForViewMode(0));
        assertEquals(0, DiLink5PlatformHelper.aisByteForViewMode(1));
        assertEquals(1, DiLink5PlatformHelper.aisByteForViewMode(2));
        assertEquals(2, DiLink5PlatformHelper.aisByteForViewMode(3));
        assertEquals(3, DiLink5PlatformHelper.aisByteForViewMode(4));
        assertEquals(6, DiLink5PlatformHelper.aisByteForViewMode(6));
        assertEquals(6, DiLink5PlatformHelper.aisByteForViewMode(9));
        assertEquals(4, DiLink5PlatformHelper.defaultAisCameraId());
    }

    @Test
    public void inferSharkModelUsesSharkProfile() {
        assertEquals(CameraProfiles.PROFILE_DILINK5_SHARK,
                CameraProfiles.infer("BYD Shark 6").getId());
        assertEquals(CameraProfiles.PROFILE_DILINK5_SEALION7,
                CameraProfiles.infer("Sealion 7").getId());
    }

    @Test
    public void dilink5LogicalMappingsAreFrontRightRearLeft() {
        CameraProfile profile = CameraProfiles.get(CameraProfiles.PROFILE_DILINK5_SHARK);
        assertEquals(Integer.valueOf(0),
                profile.getDefaultRoleMappings().get(CameraRole.PANO_FRONT).getCameraId());
        assertEquals(Integer.valueOf(1),
                profile.getDefaultRoleMappings().get(CameraRole.PANO_RIGHT).getCameraId());
        assertEquals(Integer.valueOf(2),
                profile.getDefaultRoleMappings().get(CameraRole.PANO_REAR).getCameraId());
        assertEquals(Integer.valueOf(3),
                profile.getDefaultRoleMappings().get(CameraRole.PANO_LEFT).getCameraId());
    }

    @Test
    public void isExecCapableRejectsMediaRw() {
        assertFalse(DiLink5QCarCamBackend.isExecCapableLocation(
                "/storage/emulated/0/Android/data/com.overdrive.app/files/daemon/fast_cam_capture"));
        assertTrue(DiLink5QCarCamBackend.isExecCapableLocation(
                "/data/app/~~abc/com.overdrive.app-xyz/lib/arm64/libfast_cam_capture.so"));
        assertTrue(DiLink5QCarCamBackend.isExecCapableLocation(
                "/data/local/tmp/fast_cam_capture"));
    }
}
