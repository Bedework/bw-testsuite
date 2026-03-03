package org.bedework.testsuite.webtest;

import org.bedework.base.exc.BedeworkException;
import org.bedework.util.properties.PlaceHolderProperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestProperties {
  public static final String overridePropfileSysProperty =
      "org.bedework.testsuite.webtest.overrides";

  final PlaceHolderProperties props;

  TestProperties() {
    final var newProps = new PlaceHolderProperties();
    try (final InputStream stream =
             getClass().getResourceAsStream("/webtest.properties")) {
      newProps.load(stream);
    } catch (final IOException e) {
      throw new BedeworkException(e);
    }

    final var overrides =
        System.getProperty(overridePropfileSysProperty);

    if (overrides == null) {
      props = newProps;
    } else {
      final var overrideProps = new PlaceHolderProperties(newProps);

      try (final InputStream stream =
               new FileInputStream(overrides)) {
        overrideProps.load(stream);
      } catch (final IOException e) {
        throw new BedeworkException(e);
      }

      props = overrideProps;
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
