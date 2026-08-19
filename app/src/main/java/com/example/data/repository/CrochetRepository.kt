package com.example.data.repository

import com.example.data.database.ProjectDao
import com.example.data.database.YarnDao
import com.example.data.model.Project
import com.example.data.model.YarnItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CrochetRepository(
    private val projectDao: ProjectDao,
    private val yarnDao: YarnDao
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val latestActiveProject: Flow<Project?> = projectDao.getLatestActiveProject()
    val allYarn: Flow<List<YarnItem>> = yarnDao.getAllYarn()

    suspend fun getProjectById(id: Long): Project? = projectDao.getProjectById(id)

    suspend fun saveProject(project: Project): Long {
        return if (project.id == 0L) {
            projectDao.insertProject(project)
        } else {
            projectDao.updateProject(project)
            project.id
        }
    }

    suspend fun updateProjectRow(id: Long, newRow: Int) {
        projectDao.updateProjectRow(id, newRow)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }

    suspend fun removeExampleProjects() {
        projectDao.deleteExampleProjects()
    }

    suspend fun deleteAllProjects() {
        projectDao.deleteAllProjects()
    }

    suspend fun saveYarn(yarnItem: YarnItem): Long {
        return if (yarnItem.id == 0L) {
            yarnDao.insertYarn(yarnItem)
        } else {
            yarnDao.updateYarn(yarnItem)
            yarnItem.id
        }
    }

    suspend fun deleteYarn(yarnItem: YarnItem) {
        yarnDao.deleteYarn(yarnItem)
    }

    suspend fun restoreProjects(projects: List<Project>) {
        for (p in projects) {
            saveProject(p)
        }
    }

    suspend fun restoreYarnItems(yarnItems: List<YarnItem>) {
        for (y in yarnItems) {
            saveYarn(y)
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        // Do not seed default example projects per user request
        val yarnList = allYarn.first()
        if (yarnList.isEmpty()) {
            yarnDao.insertYarn(
                YarnItem(
                    brand = "Loop Wool Co.",
                    colorway = "Terracotta Glow",
                    weight = "Worsted (4)",
                    skeins = 3.5f,
                    gramsPerSkein = 100,
                    yardsPerSkein = 210,
                    fiberContent = "100% Peruvian Highland Wool",
                    colorHex = "#E07A5F",
                    notes = "Dye Lot #2026A"
                )
            )
            yarnDao.insertYarn(
                YarnItem(
                    brand = "Earthy Stitches",
                    colorway = "Sage Whisper",
                    weight = "DK Weight (3)",
                    skeins = 2.0f,
                    gramsPerSkein = 100,
                    yardsPerSkein = 250,
                    fiberContent = "70% Organic Cotton / 30% Linen",
                    colorHex = "#81B29A",
                    notes = "Soft matte texture"
                )
            )
            yarnDao.insertYarn(
                YarnItem(
                    brand = "Honeycomb Yarns",
                    colorway = "Honey Mustard",
                    weight = "Worsted (4)",
                    skeins = 4.0f,
                    gramsPerSkein = 100,
                    yardsPerSkein = 190,
                    fiberContent = "100% Superwash Merino",
                    colorHex = "#F2CC8F",
                    notes = "Ideal for cardigans"
                )
            )
        }
    }
}
