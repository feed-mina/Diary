package com.domain.demo_backend.ui.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ui_metadata")
@Getter
@Setter
@NoArgsConstructor
public class UiMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ui_id", nullable = false)
    private Long uiId;

    @Column(name = "screen_id", nullable = false, length = 50)
    private String screenId;

    @Column(name = "component_id", nullable = false, length = 50)
    private String componentId;

    @Column(name = "label_text", nullable = false, length = 100)
    private String labelText;

    @Column(name = "component_type", nullable = false, length = 20)
    private String componentType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name="is_required")
    private Boolean isRequired = false;

    @Column(name="is_readonly")
    private Boolean isReadonly = true;

    @Column(name="default_value")
    private String defaultValue;

    private String placeholder;

    @Column(name="created_at" , updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "css_class", length = 100)
    private String cssClass;

    @Column(name = "inline_style")
    private String inlineStyle;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "action_url")
    private String actionUrl;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }


}
