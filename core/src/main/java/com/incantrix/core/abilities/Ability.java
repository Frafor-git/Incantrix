package com.incantrix.core.abilities;

import com.incantrix.core.enums.GlobalCDType;

public interface Ability {

    public float getTotalCooldown();

    public float getId();

    public GlobalCDType getGlobalCDType();
}
