package com.turinmachin.unilife.fileinfo.event;

import java.util.List;

import lombok.Data;

@Data
public class DeleteFilesEvent {

    private final List<String> fileKeys;

}
