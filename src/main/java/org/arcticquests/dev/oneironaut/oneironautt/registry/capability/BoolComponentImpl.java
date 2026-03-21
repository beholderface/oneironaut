package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;


public class BoolComponentImpl implements IBoolComponent {
    private boolean value;

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }
}