/*
 * Copyright ThinkTank Maths Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package javax.microedition.location;

public class Landmark {

	public Landmark(String name, String description, QualifiedCoordinates coordinates, AddressInfo addressInfo)  {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the AddressInfo of the landmark.
	 *
	 * @return the AddressInfo of the landmark.
	 * @see #setAddressInfo(AddressInfo)
	 */
	public AddressInfo getAddressInfo() {
		return null;
	}

	/**
	 * Gets the landmark description.
	 *
	 * @return the description of the landmark, null if not available.
	 * @see #setDescription(String)
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Gets the landmark name.
	 *
	 * @return the name of the landmark.
	 * @see #setName(String)
	 */
	public String getName() {
		return null;
	}

	/**
	 * Gets the QualifiedCoordinates of the landmark.
	 *
	 * @return the QualifiedCoordinates of the landmark. null if not available.
	 * @see #setQualifiedCoordinates(QualifiedCoordinates)
	 */
	public QualifiedCoordinates getQualifiedCoordinates() {
		return null;
	}

	/**
	 * Sets the AddressInfo of the landmark.
	 *
	 * @param addressInfo
	 *            the AddressInfo of the landmark
	 * @see #getAddressInfo()
	 */
	public void setAddressInfo(AddressInfo addressInfo) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the description of the landmark.
	 *
	 * @param description
	 *            description for the landmark, null may be passed in to indicate that
	 *            description is not available.
	 * @see #getDescription()
	 */
	public void setDescription(String description) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the name of the landmark.
	 *
	 * @param name
	 *            name for the landmark
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @see #getName()
	 */
	public void setName(String name) throws NullPointerException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the QualifiedCoordinates of the landmark.
	 *
	 * @param coordinates
	 *            the qualified coordinates of the landmark
	 * @see #getQualifiedCoordinates()
	 */
	public void setQualifiedCoordinates(QualifiedCoordinates coordinates) {
		throw new UnsupportedOperationException();
	}
}