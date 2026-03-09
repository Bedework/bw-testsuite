package org.bedework.testsuite.webtest;

import org.bedework.util.properties.PlaceHolderProperties;

public class TestProperties {
  public static final String overridePropfileSysProperty =
      "org.bedework.testsuite.webtest.overrides";

  final PlaceHolderProperties props;

  TestProperties() {
    props = PlaceHolderProperties
        .loadPropertyFile("/webtest.properties");

    final var overridesPath =
        System.getProperty(overridePropfileSysProperty);

    if ((overridesPath != null) &&
        !overridesPath.isEmpty()) {
      final var overrrides =
          PlaceHolderProperties.
              loadWithSuperProperties(overridesPath);
      props.putAll(overrrides);
    }
  }

  public String getProperty(final String propName) {
    return props.getProperty(propName);
  }

  public void setProperty(final String name,
                          final String value) {
    props.setProperty(name, value);
  }
}
