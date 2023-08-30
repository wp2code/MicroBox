package com.wb2code.microbox;

import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.config.MicroToolGlobalConfig;
import com.wb2code.microbox.meta.MicroToolFrame;
import com.wb2code.microbox.utils.SQLiteUtil;

import java.io.IOException;

/**
 * @author lwp
 * @date 2022-11-17
 */
public class MicroBoxApplication {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (SQLiteUtil.init(false)) {
            final MicroToolFrame microToolFrame = new MicroToolFrame(MicroToolGlobalConfig.builder().title("微服务启动容器")
                    .dimension(CommonConstants.FRAME_DIMENSION).icon(CommonConstants.SYS_ICON).build());
            microToolFrame.run();
        }
    }
}
