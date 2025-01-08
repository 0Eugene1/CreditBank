package com.example.dossier.dto;

import com.example.dossier.enums.ThemeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {

    private String address;
    private ThemeEnum theme;
    private Long statementId;
    private String text;
}