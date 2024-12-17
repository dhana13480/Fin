package org.finra.rmcs.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public  class JsonStringType implements UserType {

  public int[] sqlTypes() {
    return new int[] {Types.JAVA_OBJECT};
  }

  @Override
  public int getSqlType() {
    return 0;
  }

  @Override
  public Class<?> returnedClass() {
    return String.class;
  }

  @Override
  public boolean equals(Object objX, Object objY) throws HibernateException {
    if (objX == null) {
      return objY == null;
    }
    return objX.equals(objY);
  }

  @Override
  public int hashCode(Object obj) throws HibernateException {
    return obj.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session,
      Object owner) throws SQLException {
    return null;
  }


  public Object nullSafeGet(
      ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    if (rs.getString(names[0]) == null) {
      return null;
    }
    return rs.getString(names[0]);
  }

  @Override
  public void nullSafeSet(
      PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value == null) {
      st.setNull(index, Types.OTHER);
      return;
    }
    st.setObject(index, value, Types.OTHER);
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (String) this.deepCopy(value);
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }
}
