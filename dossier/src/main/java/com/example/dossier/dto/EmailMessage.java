package com.example.dossier.dto;

import com.example.dossier.enums.ThemeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {

    private String address;
    private ThemeEnum theme;
    private UUID statementId;
    private String text;
}