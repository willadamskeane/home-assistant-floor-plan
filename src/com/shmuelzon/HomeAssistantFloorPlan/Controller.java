package com.shmuelzon.HomeAssistantFloorPlan;

// ... [Previous imports and class declarations remain the same] ...

    private String generateBaseYaml() throws IOException {
        return String.format(
            "type: picture-elements\n" +
            "image: /local/floorplan/base.%s?version=%s\n" +
            "elements:\n",
            getFloorplanImageExtention(), renderHash("base", false));
    }

    private String generateRgbLightYaml(String lightName, String imageName) throws IOException {
        Entity entity = homeAssistantEntities.get(lightName);
        Point2d pos = getEntityPosition(lightName);
        
        return String.format(
            // Luminance layer
            "  - type: custom:config-template-card\n" +
            "    variables:\n" +
            "      LIGHT_STATE: states['%s'].state\n" +
            "      BRIGHTNESS: >-\n" +
            "        states['%s'].attributes.brightness\n" +
            "    entities:\n" +
            "      - %s\n" +
            "    element:\n" +
            "      type: image\n" +
            "      image: >-\n" +
            "        /local/floorplan/%s.png?version=%s\n" +
            "      state_image:\n" +
            "        \"on\": >-\n" +
            "          /local/floorplan/%s.png?version=%s\n" +
            "      entity: %s\n" +
            "    style:\n" +
            "      mask-image: >-\n" +
            "        radial-gradient(farthest-side at %.1f%% %.1f%%, rgba(0,0,0,1) 20%%,\n" +
            "        transparent 40%%)\n" +
            "      opacity: \"${LIGHT_STATE === 'on' ? (BRIGHTNESS / 255) * 0.8 + 0.2 : '0'}\"\n" +
            "      mix-blend-mode: lighten\n" +
            "      pointer-events: none\n" +
            "      left: 50%%\n" +
            "      top: 50%%\n" +
            "      width: 100%%\n" +
            // Color layer
            "  - type: custom:config-template-card\n" +
            "    variables:\n" +
            "      LIGHT_STATE: states['%s'].state\n" +
            "      LIGHT_COLOR: states['%s'].attributes.hs_color\n" +
            "      BRIGHTNESS: >-\n" +
            "        states['%s'].attributes.brightness\n" +
            "    entities:\n" +
            "      - %s\n" +
            "    element:\n" +
            "      type: image\n" +
            "      image: >-\n" +
            "        /local/floorplan/%s.png?version=%s\n" +
            "      state_image:\n" +
            "        \"on\": >-\n" +
            "          /local/floorplan/%s.red.png?version=%s\n" +
            "      entity: %s\n" +
            "    style:\n" +
            "      mask-image: >-\n" +
            "        radial-gradient(farthest-side at %.1f%% %.1f%%, rgba(0,0,0,1) 20%%,\n" +
            "        transparent 40%%)\n" +
            "      filter: >-\n" +
            "        ${ \"opacity(\" + (LIGHT_COLOR ? LIGHT_COLOR[1] : 100) + \"%%\") + \" hue-rotate(\" +\n" +
            "        (LIGHT_COLOR ? LIGHT_COLOR[0] : 0) + \"deg)\"}\n" +
            "      opacity: \"${LIGHT_STATE === 'on' ? (BRIGHTNESS / 255) * 0.8 + 0.2 : '0'}\"\n" +
            "      mix-blend-mode: normal\n" +
            "      pointer-events: none\n" +
            "      left: 50%%\n" +
            "      top: 50%%\n" +
            "      width: 100%%\n",
            // Luminance layer parameters
            lightName, lightName, lightName,
            TRANSPARENT_IMAGE_NAME, renderHash(TRANSPARENT_IMAGE_NAME, true),
            imageName, renderHash(imageName, true),
            lightName,
            pos.x, pos.y,
            // Color layer parameters
            lightName, lightName, lightName, lightName,
            TRANSPARENT_IMAGE_NAME, renderHash(TRANSPARENT_IMAGE_NAME, true),
            imageName, renderHash(imageName + ".red", true),
            lightName,
            pos.x, pos.y);
    }

    private String generateLightYaml(List<String> groupLights, List<String> onLights, String imageName) throws IOException {
        String lightName = groupLights.get(0);
        Entity entity = homeAssistantEntities.get(lightName);
        Point2d pos = getEntityPosition(lightName);
        
        return String.format(
            "  - type: custom:config-template-card\n" +
            "    variables:\n" +
            "      LIGHT_STATE: states['%s'].state\n" +
            "      BRIGHTNESS: >-\n" +
            "        states['%s'].attributes.brightness\n" +
            "    entities:\n" +
            "      - %s\n" +
            "    element:\n" +
            "      type: image\n" +
            "      image: >-\n" +
            "        /local/floorplan/%s.png?version=%s\n" +
            "      state_image:\n" +
            "        \"on\": >-\n" +
            "          /local/floorplan/%s.png?version=%s\n" +
            "      entity: %s\n" +
            "    style:\n" +
            "      mask-image: >-\n" +
            "        radial-gradient(farthest-side at %.1f%% %.1f%%, rgba(0,0,0,1) 20%%,\n" +
            "        transparent 40%%)\n" +
            "      opacity: \"${LIGHT_STATE === 'on' ? (BRIGHTNESS / 255) * 0.8 + 0.2 : '0'}\"\n" +
            "      mix-blend-mode: lighten\n" +
            "      pointer-events: none\n" +
            "      left: 50%%\n" +
            "      top: 50%%\n" +
            "      width: 100%%\n",
            lightName, lightName, lightName,
            TRANSPARENT_IMAGE_NAME, renderHash(TRANSPARENT_IMAGE_NAME, true),
            imageName, renderHash(imageName, true),
            lightName,
            pos.x, pos.y);
    }

    private String generateEntityYaml(Entity entity) {
        if (entity.displayType == EntityDisplayType.NONE || entity.alwaysOn)
            return "";

        return String.format(
            "  - type: state-icon\n" +
            "    entity: %s\n" +
            "    title: null\n" +
            "    style:\n" +
            "      top: %.2f%%\n" +
            "      left: %.2f%%\n" +
            "      border-radius: 50%%\n" +
            "      text-align: center\n" +
            "      background-color: rgba(255, 255, 255, 0.3)\n" +
            "    tap_action:\n" +
            "      action: %s\n" +
            "    double_tap_action:\n" +
            "      action: %s\n" +
            "    hold_action:\n" +
            "      action: %s\n",
            entity.name,
            100.0 * (entity.position.y / renderHeight),
            100.0 * (entity.position.x / renderWidth),
            entityActionToYamlString.get(entity.tapAction),
            entityActionToYamlString.get(entity.doubleTapAction),
            entityActionToYamlString.get(entity.holdAction));
    }

    // ... [Rest of the class remains the same] ...