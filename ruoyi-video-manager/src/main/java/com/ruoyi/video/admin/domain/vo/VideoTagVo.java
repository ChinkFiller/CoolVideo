package com.ruoyi.video.admin.domain.vo;

import com.ruoyi.common.core.domain.entity.SysDictData;
import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VideoTagVo extends SysDictData {
    List<Long> tagVideoIds;
}
