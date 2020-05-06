CREATE VIEW OTEXA_ANNUAL_COUNTRY_VW
as
SELECT [CTRY_DESCRIPTION] AS 'Country', 
       [HEADER_TYPE]      AS 'Display', 
       [1989], 
       [1990], 
       [1991], 
       [1992], 
       [1993], 
       [1994], 
       [1995], 
       [1996], 
       [1997], 
       [1998], 
       [1999], 
       [2000], 
       [2001], 
       [2002], 
       [2003], 
       [2004], 
       [2005], 
       [2006], 
       [2007], 
       [2008], 
       [2009], 
       [2010], 
       [2011], 
       [2012], 
       [2013], 
       [2014], 
       [2015], 
       [2016], 
       [2017], 
       [2018], 
       [AUG-19], 
       [CURRENT QUARTER], 
       [FOUR QUARTERS AGO], 
       [ONE QUARTER AGO], 
       [YEAR ENDING AUG/2018], 
       [YEAR ENDING AUG/2019], 
       [YEAR ENDING JUL/2019], 
       [YEAR ENDING JUN/2019], 
       [YEAR ENDING/AUG/2018], 
       [YEAR-TO-DATE AUG/2017], 
       [YEAR-TO-DATE AUG/2018], 
       [YEAR-TO-DATE AUG/2019] 
FROM   (SELECT country.[CTRY_DESCRIPTION], 
                          header.[HEADER_DESCRIPTION], 
                          header.[HEADER_TYPE], 
                          detail.[ADJ_VAL] 
        FROM   [dbo].[OTEXA_EXE_HTS_VW] detail 
               INNER JOIN [dbo].[OTEXA_COUNTRY_REF] country 
                       ON detail.[CTRY_ID] = country.[CTRY_ID] 
               INNER JOIN [dbo].[OTEXA_HEADER_REF] header 
                       ON detail.[HEADER_ID] = header.[HEADER_ID]) AS SourceTable 
       PIVOT ( Avg(ADJ_VAL) 
             FOR [HEADER_DESCRIPTION] IN ( [1989], 
                                         [1990], 
                                         [1991], 
                                         [1992], 
                                         [1993], 
                                         [1994], 
                                         [1995], 
                                         [1996], 
                                         [1997], 
                                         [1998], 
                                         [1999], 
                                         [2000], 
                                         [2001], 
                                         [2002], 
                                         [2003], 
                                         [2004], 
                                         [2005], 
                                         [2006], 
                                         [2007], 
                                         [2008], 
                                         [2009], 
                                         [2010], 
                                         [2011], 
                                         [2012], 
                                         [2013], 
                                         [2014], 
                                         [2015], 
                                         [2016], 
                                         [2017], 
                                         [2018], 
                                         [Aug-19], 
                                         [Current Quarter], 
                                         [Four Quarters Ago], 
                                         [One Quarter Ago], 
                                         [Year Ending Aug/2018], 
                                         [Year Ending Aug/2019], 
                                         [Year Ending Jul/2019], 
                                         [Year Ending Jun/2019], 
                                         [Year Ending/Aug/2018], 
                                         [Year-to-Date Aug/2017], 
                                         [Year-to-Date Aug/2018], 
                                         [Year-to-Date Aug/2019] ) ) AS 
       PIVOTTABLE; 
GO

CREATE VIEW OTEXA_ANNUAL_CATEGORY_VW
as
SELECT CONCAT([CAT_ID], ' - ', [CAT_DESCRIPTION]) AS 'Category', 
       [HEADER_TYPE]      AS 'Display', 
       [1989], 
       [1990], 
       [1991], 
       [1992], 
       [1993], 
       [1994], 
       [1995], 
       [1996], 
       [1997], 
       [1998], 
       [1999], 
       [2000], 
       [2001], 
       [2002], 
       [2003], 
       [2004], 
       [2005], 
       [2006], 
       [2007], 
       [2008], 
       [2009], 
       [2010], 
       [2011], 
       [2012], 
       [2013], 
       [2014], 
       [2015], 
       [2016], 
       [2017], 
       [2018], 
       [AUG-19], 
       [CURRENT QUARTER], 
       [FOUR QUARTERS AGO], 
       [ONE QUARTER AGO], 
       [YEAR ENDING AUG/2018], 
       [YEAR ENDING AUG/2019], 
       [YEAR ENDING JUL/2019], 
       [YEAR ENDING JUN/2019], 
       [YEAR ENDING/AUG/2018], 
       [YEAR-TO-DATE AUG/2017], 
       [YEAR-TO-DATE AUG/2018], 
       [YEAR-TO-DATE AUG/2019] 
FROM   (SELECT  category.[CAT_ID],
                category.[CAT_DESCRIPTION], 
                header.[HEADER_DESCRIPTION], 
                header.[HEADER_TYPE], 
                detail.[ADJ_VAL] 
        FROM   [dbo].[OTEXA_EXE_HTS_VW] detail 
               INNER JOIN [dbo].[OTEXA_CATEGORY_REF] category 
                       ON detail.[CAT_ID] = category.[CAT_ID]
               INNER JOIN [dbo].[OTEXA_HEADER_REF] header 
                       ON detail.[HEADER_ID] = header.[HEADER_ID]) AS SourceTable 
       PIVOT ( SUM(ADJ_VAL) 
             FOR [HEADER_DESCRIPTION] IN ( [1989], 
                                         [1990], 
                                         [1991], 
                                         [1992], 
                                         [1993], 
                                         [1994], 
                                         [1995], 
                                         [1996], 
                                         [1997], 
                                         [1998], 
                                         [1999], 
                                         [2000], 
                                         [2001], 
                                         [2002], 
                                         [2003], 
                                         [2004], 
                                         [2005], 
                                         [2006], 
                                         [2007], 
                                         [2008], 
                                         [2009], 
                                         [2010], 
                                         [2011], 
                                         [2012], 
                                         [2013], 
                                         [2014], 
                                         [2015], 
                                         [2016], 
                                         [2017], 
                                         [2018], 
                                         [Aug-19], 
                                         [Current Quarter], 
                                         [Four Quarters Ago], 
                                         [One Quarter Ago], 
                                         [Year Ending Aug/2018], 
                                         [Year Ending Aug/2019], 
                                         [Year Ending Jul/2019], 
                                         [Year Ending Jun/2019], 
                                         [Year Ending/Aug/2018], 
                                         [Year-to-Date Aug/2017], 
                                         [Year-to-Date Aug/2018], 
                                         [Year-to-Date Aug/2019] ) ) AS 
       PIVOTTABLE; 
GO

CREATE VIEW OTEXA_ANNUAL_HTS_VW
as
SELECT CONCAT([HTS], ' - ', [DESCRIPTION]) AS 'HTS', 
       [HEADER_TYPE]      AS 'Display', 
       [1989], 
       [1990], 
       [1991], 
       [1992], 
       [1993], 
       [1994], 
       [1995], 
       [1996], 
       [1997], 
       [1998], 
       [1999], 
       [2000], 
       [2001], 
       [2002], 
       [2003], 
       [2004], 
       [2005], 
       [2006], 
       [2007], 
       [2008], 
       [2009], 
       [2010], 
       [2011], 
       [2012], 
       [2013], 
       [2014], 
       [2015], 
       [2016], 
       [2017], 
       [2018], 
       [AUG-19], 
       [CURRENT QUARTER], 
       [FOUR QUARTERS AGO], 
       [ONE QUARTER AGO], 
       [YEAR ENDING AUG/2018], 
       [YEAR ENDING AUG/2019], 
       [YEAR ENDING JUL/2019], 
       [YEAR ENDING JUN/2019], 
       [YEAR ENDING/AUG/2018], 
       [YEAR-TO-DATE AUG/2017], 
       [YEAR-TO-DATE AUG/2018], 
       [YEAR-TO-DATE AUG/2019] 
FROM   (SELECT  hts.[HTS],
                hts.[DESCRIPTION], 
                header.[HEADER_DESCRIPTION], 
                header.[HEADER_TYPE], 
                detail.[ADJ_VAL] 
        FROM   [dbo].[OTEXA_EXE_HTS_VW] detail 
               INNER JOIN [dbo].[OTEXA_HTS_REF] hts 
                       ON detail.[HTS] = hts.[HTS]
               INNER JOIN [dbo].[OTEXA_HEADER_REF] header 
                       ON detail.[HEADER_ID] = header.[HEADER_ID]) AS SourceTable 
       PIVOT ( SUM(ADJ_VAL) 
             FOR [HEADER_DESCRIPTION] IN ([1989], 
                                         [1990], 
                                         [1991], 
                                         [1992], 
                                         [1993], 
                                         [1994], 
                                         [1995], 
                                         [1996], 
                                         [1997], 
                                         [1998], 
                                         [1999], 
                                         [2000], 
                                         [2001], 
                                         [2002], 
                                         [2003], 
                                         [2004], 
                                         [2005], 
                                         [2006], 
                                         [2007], 
                                         [2008], 
                                         [2009], 
                                         [2010], 
                                         [2011], 
                                         [2012], 
                                         [2013], 
                                         [2014], 
                                         [2015], 
                                         [2016], 
                                         [2017], 
                                         [2018], 
                                         [Aug-19], 
                                         [Current Quarter], 
                                         [Four Quarters Ago], 
                                         [One Quarter Ago], 
                                         [Year Ending Aug/2018], 
                                         [Year Ending Aug/2019], 
                                         [Year Ending Jul/2019], 
                                         [Year Ending Jun/2019], 
                                         [Year Ending/Aug/2018], 
                                         [Year-to-Date Aug/2017], 
                                         [Year-to-Date Aug/2018], 
                                         [Year-to-Date Aug/2019] ) ) AS 
       PIVOTTABLE; 
GO

CREATE VIEW OTEXA_ANNUAL_CHAPTER_VW
as
SELECT CONCAT([CHAPTER], ' - ', [CHAPTER_DESCRIPTION]) AS 'Chapter', 
       [HEADER_TYPE]      AS 'Display', 
       [1989], 
       [1990], 
       [1991], 
       [1992], 
       [1993], 
       [1994], 
       [1995], 
       [1996], 
       [1997], 
       [1998], 
       [1999], 
       [2000], 
       [2001], 
       [2002], 
       [2003], 
       [2004], 
       [2005], 
       [2006], 
       [2007], 
       [2008], 
       [2009], 
       [2010], 
       [2011], 
       [2012], 
       [2013], 
       [2014], 
       [2015], 
       [2016], 
       [2017], 
       [2018], 
       [AUG-19], 
       [CURRENT QUARTER], 
       [FOUR QUARTERS AGO], 
       [ONE QUARTER AGO], 
       [YEAR ENDING AUG/2018], 
       [YEAR ENDING AUG/2019], 
       [YEAR ENDING JUL/2019], 
       [YEAR ENDING JUN/2019], 
       [YEAR ENDING/AUG/2018], 
       [YEAR-TO-DATE AUG/2017], 
       [YEAR-TO-DATE AUG/2018], 
       [YEAR-TO-DATE AUG/2019] 
FROM   (SELECT chapter.[CHAPTER],
                        chapter.[CHAPTER_DESCRIPTION], 
                        header.[HEADER_DESCRIPTION], 
                        header.[HEADER_TYPE], 
                        detail.[ADJ_VAL] 
        FROM   [dbo].[OTEXA_EXE_HTS_VW] detail
            INNER JOIN  [dbo].[OTEXA_HTS_CHAPTER__REF] chapter
                    ON detail.[HTS] = chapter.[HTS]
               INNER JOIN [dbo].[OTEXA_HEADER_REF] header 
                       ON detail.[HEADER_ID] = header.[HEADER_ID]) AS SourceTable 
       PIVOT ( SUM(ADJ_VAL) 
             FOR [HEADER_DESCRIPTION] IN ([1989], 
                                         [1990], 
                                         [1991], 
                                         [1992], 
                                         [1993], 
                                         [1994], 
                                         [1995], 
                                         [1996], 
                                         [1997], 
                                         [1998], 
                                         [1999], 
                                         [2000], 
                                         [2001], 
                                         [2002], 
                                         [2003], 
                                         [2004], 
                                         [2005], 
                                         [2006], 
                                         [2007], 
                                         [2008], 
                                         [2009], 
                                         [2010], 
                                         [2011], 
                                         [2012], 
                                         [2013], 
                                         [2014], 
                                         [2015], 
                                         [2016], 
                                         [2017], 
                                         [2018], 
                                         [Aug-19], 
                                         [Current Quarter], 
                                         [Four Quarters Ago], 
                                         [One Quarter Ago], 
                                         [Year Ending Aug/2018], 
                                         [Year Ending Aug/2019], 
                                         [Year Ending Jul/2019], 
                                         [Year Ending Jun/2019], 
                                         [Year Ending/Aug/2018], 
                                         [Year-to-Date Aug/2017], 
                                         [Year-to-Date Aug/2018], 
                                         [Year-to-Date Aug/2019] ) ) AS 
       PIVOTTABLE; 
GO
