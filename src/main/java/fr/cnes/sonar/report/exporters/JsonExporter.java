/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.exporters;

import fr.cnes.sonar.report.exceptions.BadExportationDataTypeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Exports the report in .json format
 */
public class JsonExporter implements IExporter {
    /**
     * Overridden export for json
     * @param data Data to export as String
     * @param path Path where to export the file
     * @param filename Name of the file to export
     * @return Generated file.
     * @throws BadExportationDataTypeException resources is not a String
     * @throws IOException ...
     */
    @Override
    public File export(final Object data, final String path, final String filename)
            throws BadExportationDataTypeException, IOException {

        // check if the resources format is correct
        if(!(data instanceof String)) {
            throw new BadExportationDataTypeException();
        }
        // resources casting
        final String string = (String) data;

        // set relevant variables
        final String filePath = String.format("%s/%s.json", path, filename);

        // file to write
        final File jsonFile = new File(filePath);

        // preventing leaks
        try(FileWriter fileWriter = new FileWriter(jsonFile, false)) {
            // false to overwrite.
            fileWriter.write(string);
        }

        return jsonFile;
    }
}
